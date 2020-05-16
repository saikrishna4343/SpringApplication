package com.moblileApplication.app.ws.Security;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblileApplication.app.ws.SpringApplicationContext;
import com.moblileApplication.app.ws.dto.UserDto;
import com.moblileApplication.app.ws.model.request.UserLoginRequestModel;
import com.moblileApplication.app.ws.service.UserService;

import io.jsonwebtoken.Jwts;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationFilter(AuthenticationManager authenricationManager) {
		this.authenticationManager=authenricationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			UserLoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
			
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));
		}catch (IOException e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
		
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		String userName = ((User) authResult.getPrincipal()).getUsername();
		
		//We are using jwt for building our token
		String token = Jwts.builder()
						.setSubject(userName)
						.setExpiration(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))
						.signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
						.compact();
		
		UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
		UserDto userDto=userService.getUser(userName);
		
		//Response Header will be Authorization for that we are adding value with Token_Prefix with token we genrated above
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX+token);
		response.addHeader("UserID", userDto.getUserId());
	}
}
