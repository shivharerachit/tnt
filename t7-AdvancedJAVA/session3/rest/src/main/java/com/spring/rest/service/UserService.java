package com.spring.rest.service;

import com.spring.rest.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long Id);
}