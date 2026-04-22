package com.spring.rest.controller;

import com.spring.rest.dto.ApiMessageResponse;
import com.spring.rest.dto.UserResponseDto;
import com.spring.rest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String role
    ) {
        return ResponseEntity.ok(userService.searchUsers(name, age, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> deleteUser(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean confirm
    ) {
        String message = userService.deleteUser(id, confirm);
        return ResponseEntity.ok(new ApiMessageResponse(message));
    }
}
