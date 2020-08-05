package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMSResponseDTO;
import com.mitrais.chipper.temankondangan.backendapps.microservice.feign.ProfileFeignClient;
import com.mitrais.chipper.temankondangan.backendapps.service.HelloService;

@Service
public class HelloServiceImpl implements HelloService {

	@Autowired
	ProfileFeignClient feign;
	
	@Override
	public List<ProfileMSResponseDTO> getAllProfiles(String header) {
		return feign.getProfiles(header);
	}
}
