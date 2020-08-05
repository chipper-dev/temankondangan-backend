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
	@RequestMapping(method = RequestMethod.GET, value = "/client/find/{userId}")
	Optional<ProfileMSResponseDTO> findByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
			@PathVariable("userId") Long userId);
	
	@RequestMapping(method = RequestMethod.GET, value = "/client/profiles")
    List<ProfileMSResponseDTO> getProfiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}
