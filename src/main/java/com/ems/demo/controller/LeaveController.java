package com.ems.demo.controller;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ems.demo.model.LeaveClass;
import com.ems.demo.model.Test;
import com.ems.demo.repository.LeaveRepository;
import com.ems.demo.repository.TestRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LeaveController {

	@Autowired
	private LeaveRepository lRepo;
	
	@Autowired
	private TestRepository tRepo;
	
	@GetMapping("/leave")
	public String getLeave(HttpServletRequest req, Model model, @ModelAttribute Test test, RedirectAttributes redirectAttributes) {
		
		HttpSession session = req.getSession();
		String username = (String) session.getAttribute("username");
		Test activeUser = tRepo.findByUsername(username);
		if(activeUser==null) {
			redirectAttributes.addFlashAttribute("successs","Session not found. Please Login!!!");
			return "redirect:/login";
		}
		List<Test> uList = tRepo.findAll();		
		model.addAttribute("allData",uList);
		model.addAttribute("activeuser", activeUser.getFname());
		model.addAttribute("photo",activeUser.getImg());
		
		return "leave";
	}
	
	@PostMapping("/leave")
	public String postLeave(@ModelAttribute LeaveClass leave, Model model, HttpServletRequest req, RedirectAttributes redirectAttributes) {
		
		if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long totalDays = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate())+1;
            if(totalDays<1) {
            	redirectAttributes.addFlashAttribute("error","Error! Please apply valid leave.");
            	return "redirect:/leave";
            }
            leave.setTotal((int) totalDays);  // Update the total days in the LeaveClass object
        }
		
	    HttpSession session = req.getSession();
	    String username = (String) session.getAttribute("username");
	    if(username!=null) {
	    Test activeUser = tRepo.findByUsername(username);
	    
	    if(activeUser!=null) {
	        String fname = activeUser.getFname();
	        String lname = activeUser.getLname();
	        leave.setUser(fname + " " + lname);
	        leave.setMail(activeUser.getMail());
	        
	        lRepo.save(leave);
	        redirectAttributes.addFlashAttribute("message", "Leave application submitted successfully!");
	    }else {
	    	model.addAttribute("success","User not found");
	    	return "login";
	    }
	    }else {
	    	model.addAttribute("successs", "Session expired. Please login again.");
	    	return "login";
	    }
	    
	    model.addAttribute("leave", new LeaveClass());
	    return "redirect:/leave";
	
	}
	
	
	@GetMapping("/adminleave")
	public String adminLeave(@ModelAttribute Test t , Model model , HttpServletRequest req) {
	    
		List<LeaveClass> leaveData = lRepo.findAll();
		
	    List<LeaveClass> pendingLeaves = new ArrayList<>();
	    List<LeaveClass> approvedLeaves = new ArrayList<>();
	    List<LeaveClass> declineLeaves = new ArrayList<>();
	    
	    if (leaveData != null && !leaveData.isEmpty()) {
	    	
	        for (LeaveClass leave : leaveData) {
	        	System.out.println(leave);
	            switch (leave.getStatus()) {
	                case "pending":
	                    pendingLeaves.add(leave);
	                    break;
	                case "approve":
	                    approvedLeaves.add(leave);
	                    break;
	                case "decline":
	                    declineLeaves.add(leave);
	                    break;
	                default:
	                    break;
	            }
	        }
	    }
	    model.addAttribute("pendingLeave", pendingLeaves);
	    model.addAttribute("approvedLeave", approvedLeaves);
	    model.addAttribute("declineLeave", declineLeaves);
	    
		List<Test> uList = tRepo.findAll();
		model.addAttribute("allData",uList);
		return "adminleave";
	}
	
	@GetMapping("/approveleave")
	public String approveLeave(@RequestParam("id") int leaveId, Model model) {
		
		LeaveClass leave = lRepo.findById(leaveId).orElse(null);
		if(leave!=null) {
			leave.setStatus("approve");
			lRepo.save(leave);
		}
		List<LeaveClass> lList = lRepo.findAll();
		model.addAttribute("leaveData",lList);
	    return "redirect:/adminleave";  
	}
	
	@PostMapping("/decline")
	public String declineLeave(@RequestParam("id") int leaveId, Model model, LeaveClass leave) {
		leave = lRepo.findById(leaveId).orElse(null);
		
		if(leave!=null) {
			leave.setStatus("decline");
			lRepo.save(leave);
			model.addAttribute("notification","Your leave has been declined.");
		}
		List<LeaveClass> lList = lRepo.findAll();
	    model.addAttribute("leaveData", lList);
	    return "redirect:/adminleave";  
	}
	
	@GetMapping("/myrequests")
	public String getMyRequests(Model model, Test t, HttpServletRequest req, RedirectAttributes redirectAttributes) {
	    
	    HttpSession session = req.getSession();
	    String username = (String) session.getAttribute("username");
	    if(username==null) {
	    	redirectAttributes.addFlashAttribute("successs","Session not found. Please login!");
	    	return "redirect:/login";
	    }
	    Test activeUser = tRepo.findByUsername(username);

	    model.addAttribute("photo", activeUser.getImg());
	    model.addAttribute("activeuser", activeUser.getFname());

	    List<LeaveClass> leaveData =  lRepo.findByMail(activeUser.getMail());
	    
	    List<LeaveClass> pendingLeaves = new ArrayList<>();
	    List<LeaveClass> approvedLeaves = new ArrayList<>();
	    List<LeaveClass> declineLeaves = new ArrayList<>();
	    
	    if (leaveData != null && !leaveData.isEmpty()) {
	        for (LeaveClass leave : leaveData) {
	            switch (leave.getStatus()) {
	                case "pending":
	                    pendingLeaves.add(leave);
	                    break;
	                case "approve":
	                    approvedLeaves.add(leave);
	                    break;
	                case "decline":
	                    declineLeaves.add(leave);
	                    break;
	                default:
	                    break;
	            }
	        }
	    }
	    model.addAttribute("pendingLeave", pendingLeaves);
	    model.addAttribute("approvedLeave", approvedLeaves);
	    model.addAttribute("declineLeave", declineLeaves);

	    return "myrequests";
	}
	
	@GetMapping("/removeLeave")
	public String getRemoveLeave(@RequestParam("id") int id) {
		lRepo.deleteById(id);
		return "redirect:/myrequests";
	}
	
	@GetMapping("/teamrequests")
	public String getTeamRequest() {
		
		return "teamrequests";
	}
}
