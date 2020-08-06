package com.mitrais.chipper.temankondangan.backendapps.microservice.dto;

import java.time.LocalDate;
import java.util.Date;

import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LegacyProfileRequestDTO {
	private Long profileId;
	private Long userId;
	private String fullName;
	private LocalDate dob;
	private Gender gender;
	private byte[] photoProfile;
	private String photoProfileFilename;
	private String city;
	private String aboutMe;
	private String interest;
	private String dataState;
	private String createdBy;
	private Date createdDate;
	private String lastModifiedBy;
	private Date lastModifiedDate;
}