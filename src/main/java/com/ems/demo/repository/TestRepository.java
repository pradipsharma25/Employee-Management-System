package com.ems.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ems.demo.model.Test;

public interface TestRepository extends JpaRepository<Test, Integer>{

	boolean existsByUsernameAndPassword(String username , String password);
	
	Test findByUsername(String un);
	
}
