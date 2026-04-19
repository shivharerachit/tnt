package com.spring.rest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEntity {
    private final Long id;
    private final String name;
    private final Integer age;
    private final Role role;
}
