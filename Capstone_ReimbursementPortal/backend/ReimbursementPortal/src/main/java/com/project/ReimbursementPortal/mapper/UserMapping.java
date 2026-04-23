package com.project.ReimbursementPortal.mapper;

import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;

public final class UserMapping {

    private UserMapping() {
    }

    /**
     * Converts a User entity to a UserResponseDto.
     * @param user the user entity to convert
     * @return User mapped to response DTO
     */
    public static UserResponseDto convertToDTO(final User user) {

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getManagerId(),
                user.getReporteesIds(),
                user.getCreatedAt()
        );
    }

}
