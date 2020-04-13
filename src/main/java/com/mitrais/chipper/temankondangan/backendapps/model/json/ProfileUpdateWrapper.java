package com.mitrais.chipper.temankondangan.backendapps.model.json;

import org.springframework.web.multipart.MultipartFile;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;

public class ProfileUpdateWrapper {
	
	private MultipartFile image;
    private Profile profile;
    
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}
	public Profile getProfile() {
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
    
    
}
