package com.mitrais.chipper.temankondangan.backendapps.model.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProfileUpdateWrapper {

	private MultipartFile image;
	private Long userId;
	private String fullName;
	private LocalDate dob;
	private Gender gender;
	private String city;
	private String aboutMe;
	private String interest;

}
