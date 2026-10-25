package com.zjgsu.tyt.monolith.repository;

import com.zjgsu.tyt.monolith.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
