package com.kodnest.salesnova.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kodnest.salesnova.entities.JWTToken;



public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {
	
	@Query("SELECT t from JWTToken t WHERE t.user.user_id = :user_id")
	JWTToken findByUserID(@Param("user_id") int user_id);
	
}
