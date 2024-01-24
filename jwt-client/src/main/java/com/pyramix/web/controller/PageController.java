package com.pyramix.web.controller;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.pyramix.web.model.User;

@Controller
public class PageController {

	private User user;
	
	private static RestTemplate restTemplate = new RestTemplate();
	
	private static final Logger log = Logger.getLogger(PageController.class);
	
	@GetMapping("/main")
	public String mainPage(@ModelAttribute("userLogin") final User userLogin) {
		log.info("accessing main page...");
		
		// prevent null data to overwrite user object
		if (userLogin.getId()!=null) {
			log.info(userLogin.toString());
			setUser(userLogin);			
		} else {
			log.info(getUser().toString());
		}
		
		return "/main";
	}
	
	@GetMapping("/user")
	public String userPage() {
		log.info("accessing user page...");
		log.info("using token: "+getUser().getAccessToken());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", String.format("Bearer %s", user.getAccessToken()));
		
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> result =
				restTemplate.exchange("http://localhost:8080/api/test/user", HttpMethod.GET, request, String.class);
		
		log.info("response: "+result.getBody());
		
		return "/pages/user";
	}

	@GetMapping("/admin")
	public String adminPage() {
		log.info("accessing admin page...");
		log.info("using token: "+getUser().getAccessToken());

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", String.format("Bearer %s", user.getAccessToken()));
		
		HttpEntity<String> request = new HttpEntity<String>(headers);
		
		try {
			ResponseEntity<String> result =
					restTemplate.exchange("http://localhost:8080/api/test/admin", HttpMethod.GET, request, String.class);			
			log.info("response: "+result.getBody());
		} catch (HttpClientErrorException e) {
			return "/error";
		}

		return "/pages/admin";
	}
	
	@GetMapping("/mod")
	public String modPage() {
		log.info("accessing moderator page...");
		log.info("using token: "+getUser().getAccessToken());

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", String.format("Bearer %s", user.getAccessToken()));
		
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> result =
				restTemplate.exchange("http://localhost:8080/api/test/mod", HttpMethod.GET, request, String.class);
		
		log.info("response: "+result.getBody());
		
		return "/pages/moderator";
	}
	
	@GetMapping("/logout")
	public String logout() {
		
		return "/index";
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
