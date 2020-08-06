package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMicroservicesDTO;

public interface HelloService {

	List<ProfileMicroservicesDTO> getAllProfiles(String header);

}
