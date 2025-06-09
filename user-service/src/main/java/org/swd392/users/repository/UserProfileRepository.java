package org.swd392.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swd392.users.entity.UserProfile;

import java.util.Optional;

@Repository

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // Define custom query methods if needed
    // For example, find by username or email
    Optional<UserProfile> findByUserId(Long id);
}
