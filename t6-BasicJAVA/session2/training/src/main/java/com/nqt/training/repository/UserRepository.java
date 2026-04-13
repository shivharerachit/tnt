package com.nqt.training.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.nqt.training.model.User;
import com.nqt.training.request.CreateUserRequest;

@Repository
public class UserRepository {
    
    private final Map<Long, User> userStore = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public UserRepository() {
        save(new CreateUserRequest("Aman", "aman@example.com"));
        save(new CreateUserRequest("Priya", "priya@example.com"));
        save(new CreateUserRequest("Ram", "ram@example.com"));
    }

    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    public User findById(Long id) {
        return userStore.get(id);
    }

    public User save(CreateUserRequest request) {
        Long id = sequence.incrementAndGet();
        User user = new User(id, request.getName(), request.getEmail());
        userStore.put(id, user);
        return user;
    }
}