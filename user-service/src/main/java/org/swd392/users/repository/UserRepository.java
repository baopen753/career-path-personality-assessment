package org.swd392.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.swd392.users.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findUserByEmail(String email);


}
