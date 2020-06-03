package com.mitrais.chipper.temankondangan.backendapps.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mitrais.chipper.temankondangan.backendapps.model.common.Auditable;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdBy", "createdDate", "modifiedBy", "modifiedDate" }, allowGetters = true)
@ApiModel(description = "All details about Profile. ")
@SQLDelete(sql = "UPDATE profile SET data_state = 'DELETED' WHERE profile_id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "data_state <> 'DELETED'")
public class Profile extends Auditable<String> {

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
	@Size(min = 1, max = 50)
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

	@ApiModelProperty(notes = "Profile photo filename")
	private String photoProfileFilename;

	@ApiModelProperty(notes = "Profile city")
	private String city;

	@ApiModelProperty(notes = "Profile about me")
	@Column(length = 200)
	@Size(max = 200)
	private String aboutMe;

	@ApiModelProperty(notes = "Profile interest")
	@Column(length = 200)
	@Size(max = 200)
	private String interest;

	@NotNull
	@Enumerated(EnumType.STRING)
	protected DataState dataState;

	@PreRemove
	public void deleteUser() {
		this.dataState = DataState.DELETED;
	}
}
