package com.ems.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ems.demo.model.Notice;
import com.ems.demo.repository.NoticeRepository;

@Controller
public class NoticeController {

	@Autowired
	private NoticeRepository nRepo;
	
	
	@GetMapping("notice")
	public String getNotice(Model model){
		
		List<Notice> nList = nRepo.findAll();
		model.addAttribute("allData",nList);
		model.addAttribute("notice", new Notice());
		return "notice";
	}
	
	@PostMapping("postform")
	public String postForm(@RequestParam("title") String title, @RequestParam("notice") String notices) {
		Notice notice = new Notice();
		notice.setTitle(title);
		notice.setNotice(notices);
		nRepo.save(notice);
		
		return "redirect:/notice";
	}
	
	@PostMapping("deletenotice")
	public String postDelete(@RequestParam("id") int id) {
		nRepo.deleteById(id);
		return "redirect:/notice";
	}
	
}
