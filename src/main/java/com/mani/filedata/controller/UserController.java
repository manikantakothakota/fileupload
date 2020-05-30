package com.mani.filedata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mani.filedata.entity.User;
import com.mani.filedata.service.USerService;

//@Controller
@RestController
public class UserController {

	@Autowired
	private USerService service;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("user", new User() );
		List<User> users=service.findAll();
		 model.addAttribute("users", users);
		return "views/users";
	}
	
	/*
	 * @PostMapping("/fileupload") public String uploadFile(@ModelAttribute User
	 * user,RedirectAttributes redirectAttributes) { boolean isFlag=
	 * service.saveDatafromUploadedfile(user.getFile()); if(isFlag) {
	 * redirectAttributes.addFlashAttribute("success","file uploaded successfully");
	 * }else { redirectAttributes.addFlashAttribute(
	 * "error","file uploaded not done,please try again"); } return "redirect:/"; }
	 */
	
	@PostMapping(value = "/fileupload")
	public  ResponseEntity<String>  uploadFile(@ModelAttribute User user) {
	boolean isFlag= service.saveDatafromUploadedfile(user.getFile());
	String msg;	
	if(isFlag) {
			msg="file uploaded successfully";
			return new ResponseEntity<String>(msg,HttpStatus.OK);
		}else {
			msg="file uploaded not done,please try again";
			return new ResponseEntity<String>(msg,HttpStatus.EXPECTATION_FAILED);
		}
		
	}
}
