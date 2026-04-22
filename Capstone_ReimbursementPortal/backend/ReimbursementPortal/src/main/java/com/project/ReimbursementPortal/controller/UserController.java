package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Allow cr2
//Password Encryption needs to be there.
//Restrict emails to a predefined domain (e.g., @company.com)
//Allow Admin to:
//Assign a manager to an employee
//Remove users

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequest) {
        // Call the service layer to create a new user and return the response DTO
        return userService.createUser(userRequest);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        // Call the service layer to retrieve all users and return a list of response DTOs
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        // Call the service layer to retrieve a user by ID and return the response DTO
        return userService.getUser(id);
    }

//    @PutMapping("/{id}")
//    public UserResponseDto updateUser(
//            @PathVariable Long id,
//            @RequestBody UserRequestDto userRequest
//        ) {
//        // Call the service layer to update an existing user and return the response DTO
//    }

//    @PatchMapping("/{employeeId}/manager/{managerId}")
//    public UserResponseDto assignManager(
//            @PathVariable Long employeeId,
//            @PathVariable Long managerId
//        ) {
//        // Call the service layer to assign a manager to an employee and return the updated user response DTO
//    }

    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable Long id,
            @RequestParam(required = false) boolean confirm
        ) {
        if (!confirm) {
            return "Please confirm deletion by setting the 'confirm' query parameter to true.";
        }
        // Call the service layer to delete a user by ID and return a confirmation message
        userService.deleteUser(id);
        return "User with id " + id + " has been deleted.";
    }
}
