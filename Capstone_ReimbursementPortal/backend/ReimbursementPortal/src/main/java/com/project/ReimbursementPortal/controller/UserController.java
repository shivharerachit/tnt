package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * The UserController class is responsible for handling HTTP requests related to user management.
     */
    private final UserService userService;

    /**
     * Constructor for UserController that initializes the UserService dependency.
     *
     * @param userService the user service dependency for injection
     */
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param userRequest the user data to create
     * @return the created user response
     */
    @PostMapping
    public UserResponseDto createUser(@RequestBody final UserRequestDto userRequest) {
        return userService.createUser(userRequest);
    }

    /**
     * Retrieves all users.
     *
     * @return the list of all users
     */
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a user by id.
     *
     * @param id the user id
     * @return the user response
     */
    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable final Long id) {
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

    /**
     * Deletes a user by id when deletion is confirmed.
     *
     * @param id the user id
     * @param confirm whether deletion is confirmed
     * @return a confirmation or warning message
     */
    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable final Long id,
            @RequestParam(required = false) final boolean confirm
        ) {
        if (!confirm) {
            return "Please confirm deletion by setting the 'confirm' query parameter to true.";
        }
        userService.deleteUser(id);
        return "User with id " + id + " has been deleted.";
    }
}
