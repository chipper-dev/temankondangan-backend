package com.mitrais.chipper.temankondangan.backendapps.microservice.dto;

import java.time.LocalDate;
import java.util.Date;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileLegacyResponseDTO {
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

	public ProfileLegacyResponseDTO(Profile profile) {
		this.profileId = profile.getProfileId();
		this.userId = profile.getUser().getUserId();
		this.fullName = profile.getFullName();
		this.dob = profile.getDob();
		this.gender = profile.getGender();
		this.photoProfile = profile.getPhotoProfile();
		this.photoProfileFilename = profile.getPhotoProfileFilename();
		this.city = profile.getCity();
		this.aboutMe = profile.getAboutMe();
		this.interest = profile.getInterest();
		this.dataState = profile.getDataState().toString();
		this.createdBy = profile.getCreatedBy();
		this.createdDate = profile.getCreatedDate();
		this.lastModifiedBy = profile.getLastModifiedBy();
		this.lastModifiedDate = profile.getLastModifiedDate();
	}
}