package com.kodnest.salesnova.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodnest.salesnova.entities.User;
import com.kodnest.salesnova.services.UserService;



@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	UserService userServe;
	
	

	@PostMapping("/register")
	public ResponseEntity<?> registeredUser(@RequestBody User user) {

	    System.out.println("Register API called");
	    System.out.println(user.getUsername());
	    System.out.println(user.getEmail());

	    try {

	        User register = userServe.createUser(user);

	        return ResponseEntity.ok(
	            Map.of(
	                "message","Registration Successful",
	                "user",register
	            )
	        );

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity.badRequest()
	                .body(Map.of("error", e.getMessage()));
	    }
	}
}
