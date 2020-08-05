package com.mitrais.chipper.temankondangan.backendapps.microservice.feign.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMSResponseDTO;
import com.mitrais.chipper.temankondangan.backendapps.service.HelloService;

@RestController
@RequestMapping("/hell")
public class HelloController extends CommonResource {
	private static final String HEADER_AUTH = "Authorization";
	@Autowired
	private Environment env;

	@Autowired
	HelloService hello;

	@GetMapping("/hello")
	public String index(HttpServletRequest request) {
		return "Hello from Legacy Service running at port: " + env.getProperty("local.server.port");
	}

	@GetMapping("/helloprofiles")
	public List<ProfileMSResponseDTO> getProfiles(HttpServletRequest request) {
		LOGGER.info("Get all profiles test");
		return hello.getAllProfiles(request.getHeader(HEADER_AUTH));
	}
}
