package com.pyramix.web.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pyramix.web.model.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

	private HttpResponse<String> response;
	
	private static final Logger log = Logger.getLogger(LoginController.class);
	
	@GetMapping("/login")
	public String login() {
		
		return "/login";
	}
	
	@SuppressWarnings("serial")
	@PostMapping("/authLogin")
	public String authLogin(@RequestParam String username, @RequestParam String password) 
				throws IOException, InterruptedException {
		log.info("username : "+ (username.isBlank() ? "no_value" : username));
		log.info("password : "+ (password.isBlank() ? "no_value" : password));
		
		var values = new HashMap<String, String>() {
			{
				put("username", username);
				put("password", password);
			}
		};
		
		var objectMapper = new ObjectMapper();
		String requestBody = objectMapper
				.writeValueAsString(values);
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/api/auth/signin"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
		
		response = 
				client.send(request, HttpResponse.BodyHandlers.ofString());
		log.info("response : "+(response.body().toString().isBlank() ? "no body" : response.body().toString()));
		if (response.body().toString().isBlank()) {
			return "redirect:/login?error=true";
		}
		if (response.statusCode()==HttpServletResponse.SC_BAD_REQUEST) {
			return "redirect:/login?error=true";
		} 
		
		return "redirect:/success";
	}
	
	@GetMapping("/success")
	public String loginSuccess(@ModelAttribute("userLogin") final User user, final RedirectAttributes redirectAttributes) {
		Gson gson = new GsonBuilder().create();
		User userLogin =
				gson.fromJson(response.body(), User.class);
		
		log.info("Login Success : "+userLogin.toString());
		
		// so that PageController will be able to 'see' userLogin
		redirectAttributes.addFlashAttribute("userLogin", userLogin);
		
		return "redirect:/main";
	}
}
