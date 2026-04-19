package com.spring.rest.controller;

import com.spring.rest.dto.UserDto;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    public UserController() {
    }

    @GetMapping("/users")
    public UserDto getUsers(){
        return "All Users";
    }

    @GetMapping("/users/{id}")
    public UserDto getUsersById(@PathVariable Long id){
//        return "All Users";
    }
}
