package com.mitrais.chipper.temankondangan.backendapps.microservice.feign;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMSResponseDTO;

@FeignClient(name = "profile-service")
public interface ProfileFeignClient {

	// fetch legacy profile data
	@RequestMapping(method = RequestMethod.GET, value = "/client/findByUser/{userId}")
	Optional<ProfileMSResponseDTO> findByUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
			@PathVariable("userId") Long userId);
	
	@RequestMapping(method = RequestMethod.GET, value = "/client/findall")
    List<ProfileMSResponseDTO> getProfiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}
