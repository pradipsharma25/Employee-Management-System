package com.ems.demo.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ems.demo.model.Notice;
import com.ems.demo.model.Test;
import com.ems.demo.repository.NoticeRepository;
import com.ems.demo.repository.TestRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private TestRepository tRepo;
	
	@Autowired
	private NoticeRepository nRepo;
	
	@Autowired
	private JavaMailSender jms;
	
	
	@GetMapping("/home")
	public String getHome(@ModelAttribute Test t, Model model, HttpServletRequest req) {
		
		HttpSession session = req.getSession(false);
		
		if(session == null || session.getAttribute("username")== null) {
			model.addAttribute("successs","Session not found. Please login");
			return "login";
		}
		
		else {
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
	        
	        //For Notice in Home section
	        List <Notice> nList = nRepo.findAll();
	        model.addAttribute("allNotice",nList);
			
			return "home";
		}	
	}
	
	//For Greeting of above /home directory
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
	
	@GetMapping("/tables")
	public String getTables(Model model,@ModelAttribute Test t, HttpServletRequest req) {
		
		HttpSession session = req.getSession(false);
		
		if(session == null || session.getAttribute("username")== null) {
			model.addAttribute("successs","Session not found. Please login");
			return "login";
		}
		
		else {
		
		String username = (String) session.getAttribute("username");
		
		Test activeUser = tRepo.findByUsername(username);
		
		List<Test> uList = tRepo.findAll();
		
		model.addAttribute("allData",uList);
		
		model.addAttribute("user",activeUser.getFname());
		model.addAttribute("photo", activeUser.getImg());
		
		
		return "tables";
		}
	}
	
	@GetMapping("/editform")
	public String postEdit(Model model ,@RequestParam("id") int id, @ModelAttribute Test t) {
		
		Test test = tRepo.findById(id).orElse(null);
		
		if (test != null) {
            model.addAttribute("test", test);
            model.addAttribute("photo",test.getImg());
            return "adminedit";
        }
        return "adminhome";
	}
	
	@PostMapping("/edit")
    public String postEdit(@ModelAttribute Test test, Model model,@RequestParam("file") MultipartFile file, @RequestParam("mail") String mail) {
		
		try {
			byte[]	imgByte = file.getBytes();
			String imgString = Base64.getEncoder().encodeToString(imgByte);
			test.setImg(imgString);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//mail sender program.
		SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mail);
        msg.setSubject("Edit Information");
        msg.setText("Hello "+test.getFname()+ "! Your Information has been edited:\n name:"+test.getFname()+" "+test.getLname()+"\n ");
        jms.send(msg);
        tRepo.save(test); // Update the user in the repository
        List<Test> uList = tRepo.findAll();
        model.addAttribute("allData", uList);
        model.addAttribute("test", test.getUsername());
         
        return "redirect:/adminhome";
    }
	
	
	
	@GetMapping("/adminhome")
	public String showAdminHome(Model model) {
		
		List<Test> uList = tRepo.findAll();
	    model.addAttribute("allData", uList);
	    
	    return "adminhome";
	}
	
	
	@PostMapping("/delete")
	public String deletePost(@RequestParam("id") int id, Model model,@ModelAttribute Test test) {
		
		
		tRepo.deleteById(id);
		model.addAttribute("allData",tRepo.findAll());
		model.addAttribute("test",test.getUsername());
		
		return "adminhome";
	}
	
	@GetMapping("/profile")
	public String getProfile(Model model, @ModelAttribute Test test, HttpServletRequest req) {
		
		HttpSession session = req.getSession();
		String username = (String) session.getAttribute("username");
		Test activeUser = tRepo.findByUsername(username);
		List<Test> uList = tRepo.findAll();
		model.addAttribute("allData",uList);
		model.addAttribute("activeuser", activeUser.getFname());
		model.addAttribute("lastname", activeUser.getLname());
		model.addAttribute("designation", activeUser.getDesignation());
		model.addAttribute("department", activeUser.getDepartment());
		
		model.addAttribute("photo",activeUser.getImg());
		
		return "profile";
	}
	
	
	@GetMapping("/attendance")
	public String getAttendance() {
		return "attendance";
	}
	
	
	
}
