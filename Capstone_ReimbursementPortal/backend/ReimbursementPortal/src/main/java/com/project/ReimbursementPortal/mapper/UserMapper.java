package com.project.ReimbursementPortal.mapper;

import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;

public final class UserMapper {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private UserMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts a user request DTO to a user entity.
     * @param req user request DTO
     * @return user entity
     */
    public static User toEntity(final UserRequestDto req) {
        User user =  new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setRole(UserRole.valueOf(req.getRole()));
        user.setManagerId(req.getManagerId());
        return user;
    }

    /**
     * Converts a user entity to a user response DTO.
     * @param user user entity
     * @return user response DTO
     */
    public static UserResponseDto toDTO(final User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setManagerId(user.getManagerId());
        return dto;
    }
}
