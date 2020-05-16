package com.moblileApplication.app.ws.controller;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.moblileApplication.app.ws.dto.UserDto;
import com.moblileApplication.app.ws.model.request.UserDetailsRequestModel;
import com.moblileApplication.app.ws.model.response.UserDetailsResponseModel;
import com.moblileApplication.app.ws.service.UserService;

@RestController
@RequestMapping("/users")// http://localhost:8080/users
public class UserController {
	
	@Autowired
	UserService userService;
	
	@GetMapping(path="/{id}",produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserDetailsResponseModel getUser(@PathVariable("id") String userId ) {
		
		UserDetailsResponseModel userResponse = new UserDetailsResponseModel();
		UserDto userDto = userService.getUserById(userId);
		BeanUtils.copyProperties(userDto, userResponse);
		return userResponse;
	}
	
	@PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE} ,
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserDetailsResponseModel createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		
		UserDetailsResponseModel userResponse = new UserDetailsResponseModel();
		UserDto userDto = new UserDto();
		
		if(userDetails.getFirstName().isEmpty()) throw new NullPointerException("Field should not be empty");
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto createdUser = userService.createUser(userDto);
		
		BeanUtils.copyProperties(createdUser, userResponse);
		
		return userResponse;
		
	}
	
	@PutMapping(path= "/{id}",consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserDetailsResponseModel updateUser(@PathVariable("id") String userId,@RequestBody UserDetailsRequestModel userDetails) {
		
		UserDto updatedDetails = new UserDto();
		UserDetailsResponseModel userResponse = new UserDetailsResponseModel();
		
		BeanUtils.copyProperties(userDetails, updatedDetails);
		UserDto userDto = userService.updateUser(userId,updatedDetails);
		
		BeanUtils.copyProperties(userDto, userResponse);
		return userResponse;
		
	}
	
	@DeleteMapping(path="/{id}",consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public String deleteUser(@PathVariable("id") String userId) {
		userService.deleteUser(userId);
		return "User Deleted";
		
	}
	
	@GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserDetailsResponseModel> usersList(@RequestParam(value="page",defaultValue = "0") int page,@RequestParam(value="limit",defaultValue = "1") int limit){
		List<UserDetailsResponseModel> returnList = new ArrayList<>();
		List<UserDto> users=userService.getUsers(page,limit);
		
		for(UserDto user: users) {
			UserDetailsResponseModel userResponse = new UserDetailsResponseModel();
			BeanUtils.copyProperties(user, userResponse);
			returnList.add(userResponse);
		}
		return returnList;
		
	}
}
