package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.repository.UserRepository;
import com.project.ReimbursementPortal.validation.EmailValidation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.project.ReimbursementPortal.mapper.UserMapping;

/**
 * Service layer for user-related operations.
 * <p>
 * Handles persistence-facing operations through {@link UserRepository}
 * and maps entities to DTOs using {@link UserMapping}.
 */
@Service
public class UserService {
    /**
     * Repository for user persistence operations.
     */
    private final UserRepository userRepository;

    /**
     * Validator for email-domain checks.
     */
    private final EmailValidation emailValidation;

    /**
     * Creates a new {@code UserService}.
     *
     * @param emailValidation validator for email-domain checks
     * @param userRepository repository used for CRUD operations on users
     */
    public UserService(final UserRepository userRepository, final EmailValidation emailValidation) {
        this.userRepository = userRepository;
        this.emailValidation = emailValidation;
    }

    /**
     * Retrieves all users from the data store.
     *
     * @return list of users mapped to response DTOs
     */
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapping::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by id.
     *
     * @param id identifier of the user
     * @return user mapped to response DTO
     * @throws RuntimeException if no user exists for the given id
     */
    public UserResponseDto getUser(final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserMapping.convertToDTO(user);
    }

    /**
     * Creates and persists a new user from the provided request payload.
     *
     * @param userRequest request payload containing user details
     * @return persisted user mapped to response DTO
     */
    public UserResponseDto createUser(final UserRequestDto userRequest) {
        emailValidation.validateEmail(userRequest.getEmail());
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setRole(userRequest.getRole());
        user.setManagerId(userRequest.getManagerId());
        user.setReporteesIds(userRequest.getReporteesIds());

        User savedUser = userRepository.save(user);
        return UserMapping.convertToDTO(savedUser);
    }

    /**
     * Deletes the user identified by the provided id.
     *
     * @param id identifier of the user to delete
     * @throws RuntimeException if no user exists for the given id
     */
    public void deleteUser(final Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userRepository.deleteById(id);
    }
}
