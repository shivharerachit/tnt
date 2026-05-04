package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.dto.ChangePasswordRequestDto;
import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * Service layer for handling user-related business logic.
     */
    private final UserService userService;

    /**
     * @param userService service
     */
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * @param userIdHeader X-USER-ID
     * @return user id from header
     * @throws BadRequestException if not numeric
     */
    private Long getUserId(final String userIdHeader) {
        try {
            return Long.parseLong(userIdHeader);
        } catch (Exception e) {
            throw new BadRequestException("Invalid X-USER-ID header: must be a valid number");
        }
    }

    /**
     * ADMIN only — create account.
     *
     * @param req body
     * @param userIdHeader X-USER-ID caller
     * @return saved user without password internals in DTO shape
     */
    @PostMapping
    public StandardResponseDto<UserResponseDto> createUser(
            final @Valid @RequestBody UserRequestDto req,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("POST /users callerUserId={}", userId);

        UserResponseDto res = userService.createUser(req, userId);

        LOGGER.info("POST /users success callerUserId={} createdUserId={}", userId, res.getId());

        return new StandardResponseDto<>(true, "User created successfully", res);
    }

    /**
     * ADMIN only — everyone in the DB.
     *
     * @param userIdHeader X-USER-ID
     * @return all users
     */
    @GetMapping
    public StandardResponseDto<List<UserResponseDto>> getAllUsers(
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("GET /users callerUserId={}", userId);

        return new StandardResponseDto<>(
                true,
                "Users fetched",
                userService.getAllUsers(userId)
        );
    }

    /**
     * ADMIN only — filter by role.
     *
     * @param roleParam EMPLOYEE / MANAGER / ADMIN
     * @param userIdHeader X-USER-ID
     * @return matching users
     */
    @GetMapping("/by-role")
    public StandardResponseDto<List<UserResponseDto>> getUsersByRole(
            final @RequestParam UserRole roleParam,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("GET /users/by-role callerUserId={} role={}", userId, roleParam);

        return new StandardResponseDto<>(
                true,
                "Users filtered",
                userService.getUsersByRole(roleParam, userId)
        );
    }

    /**
     * ADMIN only.
     *
     * @param userId employee (or other) getting a manager
     * @param managerId new manager id
     * @param userIdHeader X-USER-ID
     * @return updated row as DTO
     */
    @PutMapping("/{userId}/assign-manager")
    public StandardResponseDto<UserResponseDto> assignManager(
            final @PathVariable Long userId,
            final @RequestParam Long managerId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long currentUserId = getUserId(userIdHeader);

        LOGGER.info(
                "PUT /users/{}/assign-manager callerUserId={} managerId={}",
                userId,
                currentUserId,
                managerId);

        return new StandardResponseDto<>(
                true,
                "Manager assigned",
                userService.assignManager(userId, managerId, currentUserId)
        );
    }

    /**
     * ADMIN only.
     *
     * @param userId target
     * @param userIdHeader X-USER-ID
     * @return empty data on success
     */
    @DeleteMapping("/{userId}")
    public StandardResponseDto<Void> deleteUser(
            final @PathVariable Long userId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long currentUserId = getUserId(userIdHeader);

        LOGGER.info("DELETE /users/{} callerUserId={}", userId, currentUserId);

        userService.deleteUser(userId, currentUserId);

        LOGGER.info("DELETE /users/{} success callerUserId={}", userId, currentUserId);

        return new StandardResponseDto<>(true, "User deleted", null);
    }

    /**
     * MANAGER or ADMIN — direct reports for {@code managerId}.
     *
     * @param managerId manager id
     * @param userIdHeader X-USER-ID
     * @return list under that manager
     */
    @GetMapping("/manager/{managerId}")
    public StandardResponseDto<List<UserResponseDto>> getUsersUnderManager(
            final @PathVariable Long managerId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("GET /users/manager/{} callerUserId={}", managerId, userId);

        return new StandardResponseDto<>(
                true,
                "Users under manager",
                userService.getUsersUnderManager(managerId, userId)
        );
    }

    /**
     * Self-service only (path {@code userId} must equal header user).
     *
     * @param userId principal id in URL (must match session)
     * @param req current + new password
     * @param userIdHeader X-USER-ID
     * @return refreshed user dto
     */
    @PutMapping("/{userId}/change-password")
    public StandardResponseDto<UserResponseDto> changePassword(
            final @PathVariable Long userId,
            final @Valid @RequestBody ChangePasswordRequestDto req,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long currentUserId = getUserId(userIdHeader);
        if (!Objects.equals(currentUserId, userId)) {
            throw new BadRequestException("You can only change your own password");
        }

        LOGGER.info("PUT /users/{}/change-password", userId);

        UserResponseDto updated = userService.changePassword(currentUserId, req);

        LOGGER.info("PUT /users/{}/change-password success", userId);

        return new StandardResponseDto<>(
                true,
                "Password changed successfully",
                userService.changePassword(currentUserId, req)
        );
    }
}
