package com.spring.rest.repository;

import com.spring.rest.entity.Role;
import com.spring.rest.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    private final List<UserEntity> users;

    public UserRepository(List<UserEntity> users) {
        this.users = new ArrayList<>();
        seedUsers();
    }

    private void seedUsers() {
        users.add(new UserEntity(1L, "Priya", 30, Role.USER));
        users.add(new UserEntity(2L, "Aman", 25, Role.ADMIN));
        users.add(new UserEntity(3L, "Rohit", 30, Role.USER));
        users.add(new UserEntity(4L, "Neha", 28, Role.MANAGER));
        users.add(new UserEntity(5L, "Priya", 22, Role.USER));
        users.add(new UserEntity(6L, "Karan", 35, Role.ADMIN));
        users.add(new UserEntity(7L, "Meera", 30, Role.USER));
    }

    public List<UserEntity> findAll() {
        return new ArrayList<>(users);
    }

    public boolean deleteById(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }
}
