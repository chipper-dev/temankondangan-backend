package com.mitrais.chipper.temankondangan.backendapps.model.json;

import java.util.Date;

public class ProfileUpdateWrapper {

//	private MultipartFile image;
	private Long userId;
	private String fullName;
	private Date dob;
	private String gender;
	private String city;
	private String aboutMe;
	private String interest;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAboutMe() {
		return aboutMe;
	}
	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}
	public String getInterest() {
		return interest;
	}
	public void setInterest(String interest) {
		this.interest = interest;
	}

//	public MultipartFile getImage() {
//		return image;
//	}
//	public void setImage(MultipartFile image) {
//		this.image = image;
//	}
	

}
