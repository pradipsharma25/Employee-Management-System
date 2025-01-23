package com.ems.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ems.demo.model.LeaveClass;

public interface LeaveRepository extends JpaRepository<LeaveClass, Integer> {

	List<LeaveClass> findByMail(String user);
	
	List<LeaveClass> findByStatus(String user);
}
