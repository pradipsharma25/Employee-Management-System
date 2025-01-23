package com.ems.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ems.demo.model.Test;
import com.ems.demo.repository.TestRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private TestRepository tRepo;
	
	@GetMapping("/")
	public String getLogin() {
		
		return "login";
	}
	
	@GetMapping("/login")
	public String getLogins() {
		
		return "login";
	}
	
	@PostMapping("/login")
	public String postLogin(@ModelAttribute Test t, Model model, @RequestParam("password") String password, HttpServletRequest req) {
		
		if(password.length()==0) {
			model.addAttribute("successs","Username password required!");
			return "login";
		}
		
		String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
		
		if(tRepo.existsByUsernameAndPassword(t.getUsername(), pwd)){
			
			HttpSession session = req.getSession();
			session.setAttribute("username", t.getUsername());
			String username = (String) session.getAttribute("username");
			
			Test activeUser = tRepo.findByUsername(username);
			List<Test> uList = tRepo.findAll();
			model.addAttribute("allData",uList);
			model.addAttribute("activeuser", activeUser.getFname());
			model.addAttribute("lastname", activeUser.getLname());
			model.addAttribute("designation", activeUser.getDesignation());
			model.addAttribute("department", activeUser.getDepartment());
			
			
			model.addAttribute("photo",activeUser.getImg());
			
			//For Date and Time in Dashboard.
			LocalDate today = LocalDate.now();
	        // Format the date and time
	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
	        String formattedDate = today.format(dateFormatter);
	        String greeting = getGreeting();
	        model.addAttribute("date", formattedDate);
	        model.addAttribute("greeting", greeting);
			
			return "redirect:/home";
		}
		else {
			model.addAttribute("successs","Incorrect password. Please try again!");
			return "login";
		}	
	}

	
	@GetMapping("/logout")
	public String getLogout(Model model, HttpServletRequest req) {
		
		HttpSession session = req.getSession();
		session.invalidate();
		model.addAttribute("success","Successfully logout!");
		return "login";
	}
	
	@GetMapping("/google")
	public String getGoogleLogin(Model model) {
		
		model.addAttribute("success","This features is currently unavailable. We will surely add on next project 😉");
		return "login";
	}
	
    public static String getGreeting() {
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.NOON)) {
            return "Good Morning";
        } else if (now.isBefore(LocalTime.of(18, 0))) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    
    }
    @GetMapping("/forgot")
    public String getForgot(Model model) {
    	
    	model.addAttribute("success","Please contact administrator!!");
    	return "login";
    }
    
    @GetMapping("/adminlogin")
    public String getAdminLogin() {
    	
    	return "adminlogin";
    }
    
    @PostMapping("/adminhome")
    public String postAdminHome(Model model, @RequestParam("username") String username, @RequestParam("password") String password) {
    	if(username.equals("pradip")&& password.equals("Admin@123")) {
		List<Test> uList = tRepo.findAll();
		model.addAttribute("allData",uList);
		
    	return "adminhome";
    	}else {
    		model.addAttribute("successs", "Incorrect Username and Password");
    		return "adminlogin";
    	}
    }
 }
