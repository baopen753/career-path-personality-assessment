package org.swd392.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swd392.users.entity.User;
@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
