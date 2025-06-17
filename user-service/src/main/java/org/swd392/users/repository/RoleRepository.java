package org.swd392.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swd392.users.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
