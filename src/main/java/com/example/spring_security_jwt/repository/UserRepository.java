package com.example.spring_security_jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_security_jwt.model.MyUser;
import java.util.Optional;


public interface UserRepository extends JpaRepository<MyUser, Long> {
	Optional<MyUser> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
}
