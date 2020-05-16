package com.moblileApplication.app.ws.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.moblileApplication.app.ws.Entity.UserEntity;
import com.moblileApplication.app.ws.Exceptions.ErrorMessages;
import com.moblileApplication.app.ws.Exceptions.UserServiceException;
import com.moblileApplication.app.ws.UserRepository.UserRepository;
import com.moblileApplication.app.ws.dto.UserDto;
import com.moblileApplication.app.ws.service.UserService;
import com.moblileApplication.app.ws.shared.Utils;



@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public UserDto createUser(UserDto userDto) {
		
		if(userRepository.findUserByEmail(userDto.getEmail()) != null) throw new RuntimeException("User Already Exists");
		// TODO Auto-generated method stub
		UserEntity userEntity = new UserEntity();
		UserDto returnDto=new UserDto();
		UserEntity userEntity2 = new UserEntity();
		BeanUtils.copyProperties(userDto, userEntity);
		
		
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		userEntity.setUserId(utils.generateUserId(20));
		
		userEntity2=userRepository.save(userEntity);
		BeanUtils.copyProperties(userEntity2, returnDto);
		return returnDto;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UserEntity userEntity=userRepository.findUserByEmail(email);
		
		if(userEntity == null) {
			throw new UsernameNotFoundException("User with "+email+" not found");
		}
		
		
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new  ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		// TODO Auto-generated method stub
		UserEntity userEntity = new UserEntity();
		UserDto userDto = new UserDto();
		userEntity = userRepository.findUserByEmail(email);
		if(userEntity == null) throw new UsernameNotFoundException("User with "+email+" not found");
		BeanUtils.copyProperties(userEntity, userDto);
		return userDto;
	}

	@Override
	public UserDto getUserById(String userId) {
		// TODO Auto-generated method stub
		
		UserDto userDto = new UserDto();
		UserEntity userEntity = userRepository.findUserByUserId(userId);
		if(userEntity == null)throw new UsernameNotFoundException("User not found");
		
		BeanUtils.copyProperties(userEntity, userDto);
		return userDto;
	}

	@Override
	public UserDto updateUser(String userId, UserDto updatedDetails) {
		// TODO Auto-generated method stub
		UserDto userDto = new UserDto();
		UserEntity userEntity=userRepository.findUserByUserId(userId);
		if(userEntity==null)throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userEntity.setFirstName(updatedDetails.getFirstName());
		userEntity.setLastName(updatedDetails.getLastName());
		
		UserEntity updatedUserDetails=userRepository.save(userEntity);
		BeanUtils.copyProperties(updatedUserDetails, userDto);
		
		return userDto;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity=userRepository.findUserByUserId(userId);
		if(userEntity == null)throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userRepository.deleteByUserId(userId);
		
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		// TODO Auto-generated method stub
		
		List<UserDto> returnList = new ArrayList<UserDto>();
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> userPage=userRepository.findAll(pageableRequest);
		
		List<UserEntity> userListEntity = userPage.getContent();
		
		for(UserEntity user:userListEntity) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(user, userDto);
			returnList.add(userDto);
		}
		return returnList;
	}

}
