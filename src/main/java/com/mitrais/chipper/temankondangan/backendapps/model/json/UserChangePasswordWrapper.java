package com.mitrais.chipper.temankondangan.backendapps.model.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserChangePasswordWrapper {

	private Long userId;
	private String oldPassword;
	private String newPassword;
	private String confirmNewPassword;
}
