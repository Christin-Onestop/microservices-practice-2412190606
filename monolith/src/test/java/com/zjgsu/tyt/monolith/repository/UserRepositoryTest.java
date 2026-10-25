package com.zjgsu.tyt.monolith.repository;

import com.zjgsu.tyt.monolith.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByUsername() {
        User user = new User("test_alice", "STUDENT");
        User saved = userRepository.saveAndFlush(user);

        assertNotNull(saved.getId());

        Optional<User> result =
                userRepository.findByUsername("test_alice");

        assertTrue(result.isPresent());
        assertEquals("STUDENT", result.get().getRole());
    }

    @Test
    void shouldReturnEmptyForUnknownUsername() {
        assertTrue(
                userRepository.findByUsername("unknown_user").isEmpty()
        );
    }
}