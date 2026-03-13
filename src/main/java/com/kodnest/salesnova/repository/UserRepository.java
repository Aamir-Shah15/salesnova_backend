package com.kodnest.salesnova.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kodnest.salesnova.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	Optional<User> findByEmail(String email);
	
	Optional<User> findByUsername(String username);
	
	
}
