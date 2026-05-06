package com.project.ReimbursementPortal.mapper;

import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void toEntityShouldMapRequestToEntity() {
        UserRequestDto req = new UserRequestDto();
        req.setName("Alice");
        req.setEmail("alice@company.com");
        req.setPassword("plain-password");
        req.setRole("EMPLOYEE");
        req.setManagerId(3L);

        User user = UserMapper.toEntity(req);

        assertEquals("Alice", user.getName());
        assertEquals("alice@company.com", user.getEmail());
        assertEquals("plain-password", user.getPassword());
        assertEquals(UserRole.EMPLOYEE, user.getRole());
        assertEquals(3L, user.getManagerId());
    }

    @Test
    void toDTOShouldMapEntityToResponse() {
        User user = new User();
        user.setId(10L);
        user.setName("Bob");
        user.setEmail("bob@company.com");
        user.setRole(UserRole.MANAGER);
        user.setManagerId(1L);

        UserResponseDto dto = UserMapper.toDTO(user);

        assertEquals(10L, dto.getId());
        assertEquals("Bob", dto.getName());
        assertEquals("bob@company.com", dto.getEmail());
        assertEquals("MANAGER", dto.getRole());
        assertEquals(1L, dto.getManagerId());
    }
}
