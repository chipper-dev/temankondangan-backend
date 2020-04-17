package com.mitrais.chipper.temankondangan.backendapps.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_id_seq_gen")
	@SequenceGenerator(name = "profile_id_seq_gen", sequenceName = "profile_id_seq", allocationSize = 1)
	@ApiModelProperty(notes = "Profile DB id")
	private Long profileId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@NotEmpty
	@ApiModelProperty(notes = "Profile full name")
	private String fullName;

	@NotNull
	@JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
	@ApiModelProperty(notes = "Profile birth of date")
	private LocalDate dob;

	@NotNull
	@Column(length = 1)
	@ApiModelProperty(notes = "Profile gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;

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
	@CreatedDate
	@ApiModelProperty(notes = "When is the data created")
	private LocalDateTime createdDate;

	@NotEmpty
	@ApiModelProperty(notes = "Who modified the data last time")
	private String modifiedBy;

	@Column(nullable = false)
	@LastModifiedDate
	@ApiModelProperty(notes = "When is the data modified last time")
	private LocalDateTime modifiedDate;

}
