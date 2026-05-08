// Path: /home/prrrssshhh/PROJECTS/primetrade.AI/src/main/java/com/primetrade/api/service/AdminService.java
package com.primetrade.api.service;

import com.primetrade.api.exception.ResourceNotFoundException;
import com.primetrade.api.model.User;
import com.primetrade.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("id", user.getId());
            payload.put("name", user.getName());
            payload.put("email", user.getEmail());
            payload.put("role", user.getRole());
            payload.put("createdAt", user.getCreatedAt());
            return payload;
        }).toList();
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
    }
}
