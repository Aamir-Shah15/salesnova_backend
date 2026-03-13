package com.kodnest.salesnova.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodnest.salesnova.entities.User;
import com.kodnest.salesnova.repository.UserRepository;



@Service
public class UserService {
	
	private final UserRepository userRepo;
	private final BCryptPasswordEncoder passEncode;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
		this.passEncode = new BCryptPasswordEncoder();
	}

	@Transactional
	public User createUser(User user) {
		
		if(userRepo.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("username already exists");
		}

		if(userRepo.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("email already exists");
		}

		user.setPassword(passEncode.encode(user.getPassword()));

		User savedUser = userRepo.save(user);

		System.out.println("USER SAVED: " + savedUser.getUsername());

		return savedUser;
	}
}