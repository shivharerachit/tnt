package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * User service for handling user-related business logic. Injected via constructor.
     */
    private final UserService userService;

    /**
     * Creates a user controller.
     * @param userService user service to be injected
     */
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Extracts and validates the X-USER-ID header.
     *
     * @param userIdHeader the X-USER-ID header value
     * @return parsed user ID
     * @throws BadRequestException if header is invalid
     */
    private Long getUserId(final String userIdHeader) {
        try {
            return Long.parseLong(userIdHeader);
        } catch (Exception e) {
            throw new BadRequestException("Invalid X-USER-ID header: must be a valid number");
        }
    }

    /**
     * Creates a new user. Only accessible by ADMIN role.
     * Validates the request body and extracts the current user ID from the header.
     * @param req the user creation request payload
     * @param userIdHeader the X-USER-ID header containing the current user's ID
     * @return a standard response containing the created user details if successful
     */
    @PostMapping
    public StandardResponseDto<UserResponseDto> createUser(
            final @Valid @RequestBody UserRequestDto req,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        UserResponseDto res = userService.createUser(req, userId);

        return new StandardResponseDto<>(true, "User created successfully", res);
    }

    /**
     * Retrieves all users. Accessible by ADMIN and MANAGER roles.
     * @param userIdHeader the X-USER-ID header containing the current user's ID
     * @return a standard response containing a list of all users if successful
     */
    @GetMapping
    public StandardResponseDto<List<UserResponseDto>> getAllUsers(
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "Users fetched",
                userService.getAllUsers(userId)
        );
    }

    /**
     * Retrieves users filtered by role. Accessible by ADMIN and MANAGER roles.
     * @param roleParam the user role to filter by (EMPLOYEE or MANAGER)
     * @param userIdHeader the X-USER-ID header containing the current user's ID
     * @return a standard response containing a list of users with the specified role if successful
     */
    @GetMapping("/by-role")
    public StandardResponseDto<List<UserResponseDto>> getUsersByRole(
            final @RequestParam UserRole roleParam,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "Users filtered",
                userService.getUsersByRole(roleParam, userId)
        );
    }

    /**
     * Assigns a manager to a user. Only accessible by ADMIN role.
     * @param userId the ID of the user to assign a manager to
     * @param managerId the ID of the manager to be assigned
     * @param userIdHeader the X-USER-ID header containing the current user's ID
     * @return a standard response containing the updated user details if successful
     */
    @PutMapping("/{userId}/assign-manager")
    public StandardResponseDto<UserResponseDto> assignManager(
            final @PathVariable Long userId,
            final @RequestParam Long managerId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long currentUserId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "Manager assigned",
                userService.assignManager(userId, managerId, currentUserId)
        );
    }

    /**
     * Deletes a user. Only accessible by ADMIN role.
     * @param userId the ID of the user to be deleted
     * @param userIdHeader the X-USER-ID header containing the current user's ID
     * @return a standard response indicating success if the user was deleted successfully
     */
    @DeleteMapping("/{userId}")
    public StandardResponseDto<Void> deleteUser(
            final @PathVariable Long userId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long currentUserId = getUserId(userIdHeader);

        userService.deleteUser(userId, currentUserId);

        return new StandardResponseDto<>(true, "User deleted", null);
    }

    /**
     * Retrieves users under a specific manager. Accessible by ADMIN and MANAGER roles.
     * @param managerId the ID of the manager whose subordinates are to be retrieved
     * @param userIdHeader the X-USER-ID header containing the current user's ID
     * @return a standard response containing a list of users under the specified manager if successful
     */
    @GetMapping("/manager/{managerId}")
    public StandardResponseDto<List<UserResponseDto>> getUsersUnderManager(
            final @PathVariable Long managerId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "Users under manager",
                userService.getUsersUnderManager(managerId, userId)
        );
    }
}
