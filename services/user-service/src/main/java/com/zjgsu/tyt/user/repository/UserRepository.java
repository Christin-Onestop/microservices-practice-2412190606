package com.zjgsu.tyt.user.repository;

import com.zjgsu.tyt.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}