package org.swd392.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.swd392.users.entity.User;
import org.swd392.users.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PreAuthorize("(hasAnyRole('SYSTEM_ADMIN','ADMIN') or (hasAnyRole('STUDENT','EVENT_MANAGER','PARENT') and #id == authentication.principal.id))")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }


    @PreAuthorize("(hasAnyRole('SYSTEM_ADMIN','ADMIN') or (hasAnyRole('STUDENT','EVENT_MANAGER','PARENT') and #id == authentication.principal.id))")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}