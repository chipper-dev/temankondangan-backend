package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMSResponseDTO;

public interface HelloService {

	List<ProfileMSResponseDTO> getAllProfiles(String header);

}
