// Path: /home/prrrssshhh/PROJECTS/primetrade.AI/src/main/java/com/primetrade/api/repository/UserRepository.java
package com.primetrade.api.repository;

import com.primetrade.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
