package com.moblileApplication.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.moblileApplication.app.ws.dto.UserDto;

public interface UserService extends UserDetailsService{
	UserDto createUser(UserDto userDto);
	UserDto getUser(String email);
	UserDto getUserById(String userId);
	UserDto updateUser(String userId, UserDto updatedDetails);
	void deleteUser(String userId);
	List<UserDto> getUsers(int page, int limit);
}
