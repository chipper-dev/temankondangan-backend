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
public class ProfileMSResponseDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private LocalDate dob;
    private Gender gender;
    private String city;
    private String aboutMe;
    private String interest;
	private byte[] photoProfile;
	private String photoProfileFilename;
    protected boolean deleted;
    private String createdBy;
	private Date createdDate;
	private String lastModifiedBy;
	private Date lastModifiedDate;
	
}
