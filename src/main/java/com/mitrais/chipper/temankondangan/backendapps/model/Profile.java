package com.mitrais.chipper.temankondangan.backendapps.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "profile")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdBy", "createdDate", "modifiedBy", "modifiedDate" }, allowGetters = true)
@ApiModel(description = "All details about Profile. ")
public class Profile {

	public Profile(Users user, String fullName, Date dob, String gender, String createdBy, Date createdDate,
			String modifiedBy, Date modifiedDate) {

		this.user = user;
		this.fullName = fullName;
		this.dob = dob;
		this.gender = gender;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
	}

	public Profile(Users user, String fullName, Date dob, String gender, byte[] photoProfile, String createdBy,
			Date createdDate, String modifiedBy, Date modifiedDate) {
		this.user = user;
		this.fullName = fullName;
		this.dob = dob;
		this.gender = gender;
		this.photoProfile = photoProfile;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
	}

	@Id
	@NotNull
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

	@NotNull
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

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "When is the data created")
	private Date createdDate;

	@NotEmpty
	@ApiModelProperty(notes = "Who modified the data last time")
	private String modifiedBy;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@ApiModelProperty(notes = "When is the data modified last time")
	private Date modifiedDate;

}
