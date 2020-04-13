package com.mitrais.chipper.temankondangan.backendapps.model;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdBy", "createdAt", "modifiedBy", "updatedAt" }, allowGetters = true)
@ApiModel(description = "All details about User. ")
public class Users {

	public Users(String email,
				 String passwordHashed,
				 String createdBy, Date createdDate, String modifiedBy, Date modifiedDate) {
		this.email = email;
		this.passwordHashed = passwordHashed;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
	}

	public Users() { }

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq_gen")
    @SequenceGenerator(name = "user_id_seq_gen", sequenceName = "user_id_seq", allocationSize = 1)
    @ApiModelProperty(notes = "User DB id")
	private Long userId;

	@NotEmpty
	@ApiModelProperty(notes = "User email")
	private String email;

	@NotEmpty
	@ApiModelProperty(notes = "User hashed password")
	@JsonIgnore
	private String passwordHashed;

	@NotEmpty
	@ApiModelProperty(notes = "Who created the data")
	private String createdBy;

	@NotNull
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@ApiModelProperty(notes = "When is the data created")
	private Date createdDate;

	@NotEmpty
	@ApiModelProperty(notes = "Who modified the data last time")
	private String modifiedBy;

	@NotNull
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	@ApiModelProperty(notes = "When is the data modified last time")
	private Date modifiedDate;

}
