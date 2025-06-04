package org.swp392.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swp392.users.entity.User;
import org.swp392.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    public User createUser(User user) {
        return userRepository.save(user);
    }
    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setStatus(updatedUser.isStatus());
            return userRepository.save(existingUser);
        });
    }
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
