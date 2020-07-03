package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptedApplicantResponseWrapper {

	private Long userId;
	private String fullName;
	private Gender gender;
	private String photoProfileUrl;
	private boolean isRated;

}
