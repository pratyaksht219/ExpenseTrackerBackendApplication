package com.example.expensetracker.Repository;

import com.example.expensetracker.Entity.Approle;
import com.example.expensetracker.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(Approle roleName);
}
