package com.mitrais.chipper.temankondangan.backendapps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)

@JsonIgnoreProperties(value = { "createdBy", "createdDate", "modifiedBy", "modifiedDate" }, allowGetters = true)
@ApiModel(description = "All details about User. ")
public class User {

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq_gen")
	@SequenceGenerator(name = "user_id_seq_gen", sequenceName = "user_id_seq", allocationSize = 1)
	@ApiModelProperty(notes = "User DB id")
	private Long userId;

	@NotEmpty
	@ApiModelProperty(notes = "User email")
	private String email;

	@ApiModelProperty(notes = "User hashed password")
	@JsonIgnore
	private String passwordHashed;

	@NotEmpty
	@ApiModelProperty(notes = "Who created the data")
	private String createdBy;

	@NotNull
	@Column(nullable = false, updatable = false)
	@CreatedDate
	@ApiModelProperty(notes = "When is the data created")
	private LocalDateTime createdDate;

	@NotEmpty
	@ApiModelProperty(notes = "Who modified the data last time")
	private String modifiedBy;

	@NotNull
	@Column(nullable = false)
	@LastModifiedDate
	@ApiModelProperty(notes = "When is the data modified last time")
	private LocalDateTime modifiedDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;

	private String providerId;

}
