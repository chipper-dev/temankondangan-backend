package com.mitrais.chipper.temankondangan.backendapps.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mitrais.chipper.temankondangan.backendapps.model.common.Auditable;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdBy", "createdDate", "modifiedBy", "modifiedDate" }, allowGetters = true)
@ApiModel(description = "All details about User. ")
@SQLDelete(sql = "UPDATE users SET data_state = 'DELETED' WHERE user_id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "data_state <> 'DELETED'")
public class User extends Auditable<String> {

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

	@NotNull
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;

	private String providerId;

	@NotNull
	@Enumerated(EnumType.STRING)
	private DataState dataState;

	@PreRemove
	public void deleteUser() {
		this.dataState = DataState.DELETED;
	}
}
