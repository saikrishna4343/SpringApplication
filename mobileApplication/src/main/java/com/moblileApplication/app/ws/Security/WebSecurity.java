package com.moblileApplication.app.ws.Security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.moblileApplication.app.ws.service.UserService;




@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{
	private final UserService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	//Second Method to get called
	//Checks for request which are allowed without secret key Authorization 
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		 http.csrf()
		 .disable()
		 .authorizeRequests()
		 .antMatchers(HttpMethod.POST,SecurityConstants.SIGN_UP_URL)
		 .permitAll()
		 .anyRequest()
		 .authenticated()//Calls getAuthenticationFilter Method 
		 .and()
		 .addFilter(getAuthenticationFilter())// Adding filter from AuthenticationFilter class authenticationManager all sub classes will be automatically invoked
		 .addFilter(new AuthorizationFilter(authenticationManager()))
		 .sessionManagement()
		 .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		 
	}
	
	//First Method to get called for Security
	@Override
	public void configure(AuthenticationManagerBuilder auth)throws Exception{
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	public AuthenticationFilter getAuthenticationFilter() throws Exception{
		
		final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl("/users/login");
		return filter;
		
	}
}
