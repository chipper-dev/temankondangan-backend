package com.mitrais.chipper.temankondangan.backendapps.model;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Entity
@Table(name = "profile")
//@EntityListeners(AuditingEntityListener.class)
//@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
@ApiModel(description = "All details about Profile. ")
public class Profile {

	public Profile(Users user, String fullName, Date dob, String gender) {
		this.user = user;
		this.fullName = fullName;
		this.dob = dob;
		this.gender = gender;
	}

	public Profile() { }

	@Id
	@NotEmpty
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_id_seq_gen")
    @SequenceGenerator(name = "profile_id_seq_gen", sequenceName = "profile_id_seq", allocationSize = 1)
    @ApiModelProperty(notes = "Profile DB id")
	private Long profileId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users user;

	@NotEmpty
	@ApiModelProperty(notes = "Profile full name")
	private String fullName;

	@NotEmpty
	@JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
	@Temporal(javax.persistence.TemporalType.DATE)
	@ApiModelProperty(notes = "Profile birth of date")
	private Date dob;

	@NotEmpty
	@Column(length = 1)
	@ApiModelProperty(notes = "Profile gender")
	private String gender;

	@Lob
	@ApiModelProperty(notes = "Profile photo profile data byte")
	private byte[] photoProfile;

	@ApiModelProperty(notes = "Profile city")
	private String city;

	@ApiModelProperty(notes = "Profile about me")
	private String aboutMe;

	@ApiModelProperty(notes = "Profile interest")
	private String interest;
	@NotEmpty
	@ApiModelProperty(notes = "Who created the data")
	private String createdBy;

	@NotEmpty
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "When is the data created")
	private Date createdDate;

	@NotEmpty
	@ApiModelProperty(notes = "Who modified the data last time")
	private String modifiedBy;

	@NotEmpty
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@ApiModelProperty(notes = "When is the data modified last time")
	private Date modifiedDate;

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
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

	public byte[] getPhotoProfile() {
		return photoProfile;
	}

	public void setPhotoProfile(byte[] photoProfile) {
		this.photoProfile = photoProfile;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
