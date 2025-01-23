package com.ems.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ems.demo.model.Notice;


public interface NoticeRepository extends JpaRepository<Notice, Integer> {

}
