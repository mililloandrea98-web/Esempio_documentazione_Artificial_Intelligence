package com.example.usercrud.service;

import com.example.usercrud.dto.UserRequest;
import com.example.usercrud.dto.UserResponse;
import com.example.usercrud.exception.UserNotFoundException;
import com.example.usercrud.model.User;
import com.example.usercrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(getUserOrThrow(id));
    }

    public UserResponse create(UserRequest request) {
        User user = new User(request.getName(), request.getEmail());
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse update(Long id, UserRequest request) {
        User existing = getUserOrThrow(id);
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        return UserResponse.from(userRepository.save(existing));
    }

    public void delete(Long id) {
        User existing = getUserOrThrow(id);
        userRepository.delete(existing);
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
