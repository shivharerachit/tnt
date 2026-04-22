package com.spring.rest.entity;

public class UserEntity {
    private final Long id;
    private final String name;
    private final Integer age;
    private final Role role;

    public UserEntity(Long id, String name, Integer age, Role role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Role getRole() {
        return role;
    }
}
