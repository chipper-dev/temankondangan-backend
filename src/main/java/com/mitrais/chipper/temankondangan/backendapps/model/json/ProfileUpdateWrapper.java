package com.mitrais.chipper.temankondangan.backendapps.model.json;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProfileUpdateWrapper {

	private MultipartFile image;
	private String city;
	private String aboutMe;
	private String interest;

}
