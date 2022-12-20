package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	    
	@GetMapping("/users")
	public String listAll(Model model) {
		return listByPage(1,model, "id", "asc", null);
	}
	
	@GetMapping("/users/page/{pageNumber}")
	public String listByPage(@PathVariable(name= "pageNumber") int pageNumber, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {

		Page<User> page = service.listByPage(pageNumber,sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
		
		long startCount = (pageNumber - 1) * service.USERS_PER_PAGE + 1;
		long endCount = startCount + service.USERS_PER_PAGE - 1;
		if ( endCount > page.getTotalElements()){
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("endCount", endCount);
		model.addAttribute("startCount", startCount);
		model.addAttribute("listUsers",listUsers);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		User user = new User();
		List<Role> listRoles = service.listRoles();
		String pageTitle = "Create New User";
		model.addAttribute("listRoles",listRoles);
		model.addAttribute("pageTitle",pageTitle);
		model.addAttribute("user", user);
		return "user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
//		System.out.println(user.toString());
//		System.out.println(multipartFile.getOriginalFilename());
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
	
		//service.save(user);
		//redirectAttributes.addFlashAttribute("message", "The user ID: " + user.getId() + " has been saved successfulle.");
		
		return "redirect:/users";
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name="id") Integer id, 
			Model model,
			RedirectAttributes redirectAttributes ) {
		try {
			User user = service.getUserbyId(id);
			String pageTitle = "Update the User: " + id;
			List<Role> listRoles = service.listRoles();
			model.addAttribute("listRoles",listRoles);
			model.addAttribute("pageTitle",pageTitle);
			model.addAttribute("user", user);
			return "user_form";
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/users";
		}
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name="id") Integer id, 
			Model model,
			RedirectAttributes redirectAttributes ) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			
		}
		return "redirect:/users";
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes){
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been " + status);
		return "redirect:/users";
		
	}
	

}
