package com.mitrais.chipper.temankondangan.backendapps.microservice.feign;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.LegacyProfileRequestDTO;
import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMicroservicesDTO;

@FeignClient(name = "profile-service")
@RequestMapping(value = "/client")
public interface ProfileFeignClient {

	@GetMapping(value = "/findByUser/{userId}")
	Optional<ProfileMicroservicesDTO> findByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
			@PathVariable("userId") Long userId);

	@GetMapping("/find/{id}")
	Optional<ProfileMicroservicesDTO> findById(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
			@PathVariable("id") Long id);

	@GetMapping("/findPhoto/{photoProfileName}")
	Optional<ProfileMicroservicesDTO> findByPhotoProfileName(@PathVariable("photoProfileName") String photoProfileName);

	@GetMapping("/findall")
	Optional<List<ProfileMicroservicesDTO>> findAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String header);

	@PostMapping("/register")
	Optional<ProfileMicroservicesDTO> register(@RequestBody ProfileMicroservicesDTO requestDTO);

	@PutMapping("/deleteByUserId/{userId}")
	String deleteByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @PathVariable("userId") Long userId);
}
