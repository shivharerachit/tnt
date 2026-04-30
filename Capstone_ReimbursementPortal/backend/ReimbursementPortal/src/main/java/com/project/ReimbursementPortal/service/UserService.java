package com.project.ReimbursementPortal.service;

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

/**
 * User management business logic.
 */
@Service
public final class UserService {

    /**
     * User persistence operations.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder for storing hashed passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Email validator for domain validation.
     */
    private final EmailValidator emailValidator;

    /**
     * Creates a user service.
     *
     * @param userRepository user repository
     * @param passwordEncoder password encoder
     * @param emailValidator email validator
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
     * Creates a new user (ADMIN only).
     *
     * @param req request payload
     * @param currentUserId current user id
     * @return created user
     */
    public UserResponseDto createUser(final UserRequestDto req, final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can create users");
        }

        // Email validation
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

        // DTO → Entity
        User user = UserMapper.toEntity(req);
        user.setRole(newUserRole);

        // Password encoding
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userRepository.save(user);

        // Defensive: avoid a (rare) self-assignment if generated id happens to match provided managerId
        if (saved.getManagerId() != null && saved.getManagerId().equals(saved.getId())) {
            throw new BadRequestException("User cannot be their own manager");
        }

        // Entity → DTO
        return UserMapper.toDTO(saved);
    }

    /**
     * Returns all users (ADMIN only).
     *
     * @param currentUserId current user id
     * @return list of users
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
     * Returns users filtered by role (ADMIN only).
     *
     * @param role role to filter
     * @param currentUserId current user id
     * @return list of users
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
     * Assigns a manager to a user (ADMIN only).
     *
     * @param userId user id
     * @param managerId manager id
     * @param currentUserId current user id
     * @return updated user
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

        if (manager.getRole() != UserRole.MANAGER) {
            throw new BadRequestException("Assigned user is not a manager");
        }

        validateNoManagementCycle(user.getId(), manager);

        user.setManagerId(managerId);

        return UserMapper.toDTO(userRepository.save(user));
    }

    /**
     * Validates that assigning the proposed manager to the user does not create a management cycle.
     * @param userId the ID of the user being assigned a manager
     * @param proposedManager the proposed manager user entity
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
                // Existing cycle in DB; prevent creating/continuing it
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
     * Deletes a user by id (ADMIN only).
     *
     * @param userId user id
     * @param currentUserId current user id
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
     * Returns all users who report to the given manager (MANAGER/ADMIN).
     *
     * @param managerId manager id
     * @param currentUserId current user id
     * @return list of users
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
     * Returns any ADMIN id (used as fallback reviewer).
     *
     * @return admin id
     */
    public Long getAnyAdminId() {

        return userRepository.findByRole(UserRole.ADMIN)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No admin found"))
                .getId();
    }

    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
