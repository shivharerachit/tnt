package com.project.ReimbursementPortal.mapper;

import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;

public final class UserMapper {

    /** static helpers only. */
    private UserMapper() {
        throw new UnsupportedOperationException("no instances");
    }

    /**
     * @param req still contains plaintext pwd — hash before persist in service layer
     * @return detached entity-ish object
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
     * @param user db row or detached copy
     * @return hides password naturally (DTO lacks field)
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
