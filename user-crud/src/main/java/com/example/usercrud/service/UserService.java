package com.example.usercrud.service;

import com.example.usercrud.exception.UserNotFoundException;
import com.example.usercrud.model.User;
import com.example.usercrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User update(Long id, User payload) {
        User existing = findById(id);
        existing.setName(payload.getName());
        existing.setEmail(payload.getEmail());
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        User existing = findById(id);
        userRepository.delete(existing);
    }
}
