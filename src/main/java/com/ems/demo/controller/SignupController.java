package com.ems.demo.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ems.demo.model.Test;
import com.ems.demo.repository.TestRepository;

@Controller
public class SignupController {

	@Autowired
	private TestRepository tRepo;	
	
	@GetMapping("/signup")
	public String getsignup() {
		return "signup";
		
	}
	
	@PostMapping("/signup")
	public String postSignup(@ModelAttribute Test test, Model model, @RequestParam("password") String password,@RequestParam("repassword") String repassword, @RequestParam("file") MultipartFile file) {
			
		if(!password.equals(repassword)) {
			model.addAttribute("errorpwd","Password did not match with each other!");
			return "signup";
		}
		if(password.length()<8) {
			model.addAttribute("errorpwd","The lenght of the password should be more than 8 Character");
			return "signup";
		}
		else {
				byte[] imgByte;
				try {
					imgByte = file.getBytes();
					String imgString = Base64.getEncoder().encodeToString(imgByte);
					test.setImg(imgString);
				} catch (IOException e) {
					model.addAttribute("errorpwd","File not found or Incorrect format");
					return "signup";
				}
				
				String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
				test.setPassword(pwd);
				
				tRepo.save(test);
				model.addAttribute("success","Successfully signup.Please Login!!");
				return "login";
		}
	}
	
}
