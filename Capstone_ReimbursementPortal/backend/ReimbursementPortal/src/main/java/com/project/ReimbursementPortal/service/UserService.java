package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.dto.ChangePasswordRequestDto;
import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.exception.EmailAlreadyExistsException;
import com.project.ReimbursementPortal.exception.ForbiddenException;
import com.project.ReimbursementPortal.exception.UserNotFoundException;
import com.project.ReimbursementPortal.mapper.UserMapper;
import com.project.ReimbursementPortal.repository.UserRepository;
import com.project.ReimbursementPortal.validator.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** CRUD-ish users + hierarchy helper for claims reviewer lookup. */
@Service
public final class UserService {

    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder for hashing new passwords and verifying current ones during password changes.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Email validator to enforce company-domain-only registrations and prevent garbage data.
     */
    private final EmailValidator emailValidator;

    /**
     * @param userRepository JDBC-backed repo bean
     * @param passwordEncoder bcrypt
     * @param emailValidator company-domain gate
     */
    public UserService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final EmailValidator emailValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
    }

    /**
     * ADMIN — hashes password, optional manager only for EMPLOYEE rows.
     *
     * @param req incoming DTO
     * @param currentUserId actor from header
     * @return saved copy without password field in response type
     */
    public UserResponseDto createUser(final UserRequestDto req, final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can create users");
        }

        emailValidator.validate(req.getEmail());

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        UserRole newUserRole;
        try {
            newUserRole = UserRole.valueOf(req.getRole());
        } catch (Exception e) {
            throw new BadRequestException("Invalid role: " + req.getRole());
        }

        if (req.getManagerId() != null) {
            if (newUserRole != UserRole.EMPLOYEE) {
                throw new BadRequestException("Only EMPLOYEE can have a manager");
            }

            User manager = userRepository.findById(req.getManagerId())
                    .orElseThrow(() -> new UserNotFoundException("Manager not found"));

            if (manager.getRole() != UserRole.MANAGER) {
                throw new BadRequestException("Assigned user is not a manager");
            }
        }

        User user = UserMapper.toEntity(req);
        user.setRole(newUserRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userRepository.save(user);

        if (saved.getManagerId() != null && saved.getManagerId().equals(saved.getId())) {
            throw new BadRequestException("User cannot be their own manager");
        }

        return UserMapper.toDTO(saved);
    }

    /**
     * @param currentUserId must be ADMIN
     * @return whole user table mapped to DTOs
     */
    public List<UserResponseDto> getAllUsers(final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can view all users");
        }

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * @param role enum filter
     * @param currentUserId admin id
     * @return same shape as list-all but narrowed
     */
    public List<UserResponseDto> getUsersByRole(final UserRole role, final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can filter users");
        }

        return userRepository.findByRole(role)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Refuses obvious cycles (A manages B manages A).
     *
     * @param userId employee (or non-admin) row
     * @param managerId must be MANAGER or ADMIN in our rules
     * @param currentUserId admin actor
     * @return fresh DTO after save
     */
    public UserResponseDto assignManager(final Long userId, final Long managerId, final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can assign manager");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new BadRequestException("Cannot assign a manager to ADMIN user");
        }

        if (userId.equals(managerId)) {
            throw new BadRequestException("User cannot be their own manager");
        }

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));

        if (manager.getRole() != UserRole.MANAGER && manager.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Assigned user is not a manager or admin");
        }

        validateNoManagementCycle(user.getId(), manager);

        user.setManagerId(managerId);

        return UserMapper.toDTO(userRepository.save(user));
    }

    /**
     * Climb proposedManager → managerId chain; bail if we'd hit {@code userId} again or an existing ring.
     *
     * @param userId leaf user row id
     * @param proposedManager would-be boss entity (already loaded)
     */
    private void validateNoManagementCycle(final Long userId, final User proposedManager) {
        Set<Long> visited = new HashSet<>();
        User cursor = proposedManager;

        while (cursor != null) {
            Long cursorId = cursor.getId();

            if (cursorId != null && cursorId.equals(userId)) {
                throw new BadRequestException("Invalid manager assignment: cycle detected");
            }

            if (cursorId != null && !visited.add(cursorId)) {
                throw new BadRequestException("Invalid manager assignment: existing management cycle detected");
            }

            Long nextManagerId = cursor.getManagerId();
            if (nextManagerId == null) {
                break;
            }

            cursor = userRepository.findById(nextManagerId)
                    .orElseThrow(() -> new BadRequestException(
                            "Invalid manager chain: manager not found for id " + nextManagerId
                    ));
        }
    }

    /**
     * Hard delete row — beware FK fallout if claims still point here (DB rules win).
     *
     * @param userId doomed id
     * @param currentUserId admin actor
     */
    public void deleteUser(final Long userId, final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can delete users");
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        userRepository.deleteById(userId);
    }

    /**
     * MANAGER sees own subtree; ADMIN can query arbitrary manager ids.
     *
     * @param managerId FK target in users.manager_id column
     * @param currentUserId caller guard
     * @return orphan list if nobody reports (still 200 [])
     */
    public List<UserResponseDto> getUsersUnderManager(final Long managerId, final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.MANAGER && currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Not authorized");
        }

        return userRepository.findByManagerId(managerId)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Claim submit uses this when EMPLOYEE has null manager row.
     *
     * @return first ADMIN primary key arbitrarily
     */
    public Long getAnyAdminId() {

        return userRepository.findByRole(UserRole.ADMIN)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No admin found"))
                .getId();
    }

    /**
     * Controller already checks URL id vs header match.
     *
     * @param currentUserId self
     * @param req current + new plaintext (hashed server-side only)
     * @return dto after save
     */
    public UserResponseDto changePassword(
            final Long currentUserId,
            final ChangePasswordRequestDto req
    ) {
        User user = getUser(currentUserId);

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));

        return UserMapper.toDTO(userRepository.save(user));
    }

    /**
     * @param userId PK
     * @return hydrated user
     * @throws UserNotFoundException stale id from header tinkering
     */
    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
