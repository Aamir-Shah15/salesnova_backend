package com.kodnest.salesnova.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kodnest.salesnova.entities.JWTToken;
import com.kodnest.salesnova.entities.User;
import com.kodnest.salesnova.repository.JWTTokenRepository;
import com.kodnest.salesnova.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {
	
	private final Key SIGNING_KEY;
	
	@Autowired
	private  UserRepository userRepo;
	@Autowired
	private JWTTokenRepository jwtRepo;
	private BCryptPasswordEncoder passEncode;
	
	
	// Secret Key and BcryptPassword initialization
	public AuthService(@Value("${jwt.secret}") String jwtSecret) {
		
		this.passEncode = new BCryptPasswordEncoder();
		
		this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}
	
	// Login Authentication purpose
	public User loginAuth(String email, String password) {
		
		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));
		
		if(!passEncode.matches(password, user.getPassword())) {
			throw new RuntimeException("Invalid username and password");
		} 
		return user;
	}
	
	// Generating new Tokens
	public String generateNewToken(User user) {
		
		return Jwts.builder()
				.setSubject(user.getUsername())
				.claim("role", user.getRole().name())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 3600000))
				.signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
				.compact();
	}
	
	//Generate token
	public String generateToken(User user) {

	    LocalDateTime now = LocalDateTime.now();
	    JWTToken existingToken = jwtRepo.findByUserID(user.getUser_id());

	    String token;

	    if (existingToken != null && now.isBefore(existingToken.getExpires_at())) {

	        token = existingToken.getToken();

	    } else {

	        token = generateNewToken(user);   // 🔥 FIXED

	        if (existingToken != null) {
	            jwtRepo.delete(existingToken);
	        }

	        saveToken(user, token);
	    }

	    return token;
	}
	
	// Save Token	
	public void saveToken(User user, String token) {
		JWTToken tk = new JWTToken(user,token,LocalDateTime.now().plusHours(1));
		jwtRepo.save(tk);
	}
	
}
