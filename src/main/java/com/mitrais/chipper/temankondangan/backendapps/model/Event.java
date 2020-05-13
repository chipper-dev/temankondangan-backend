package com.mitrais.chipper.temankondangan.backendapps.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdBy", "createdDate", "modifiedBy", "modifiedDate" }, allowGetters = true)
@ApiModel(description = "All details about Event. ")
@SQLDelete(sql = "UPDATE event SET data_state = 'DELETED' WHERE event_id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "data_state <> 'DELETED'")
public class Event extends Auditable<String> {

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_seq_gen")
	@SequenceGenerator(name = "event_id_seq_gen", sequenceName = "event_id_seq", allocationSize = 1)
	@ApiModelProperty(notes = "Event DB id")
	private Long eventId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@Where(clause = "data_state <> 'DELETED'")
	private User user;

	@NotEmpty
	@Column(length = 50)
	@Size(min = 1, max = 50)
	@ApiModelProperty(notes = "Event title")
	private String title;

	@NotEmpty
	@ApiModelProperty(notes = "The city where the event take place")
	private String city;

	@NotNull
	@Future
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
	@ApiModelProperty(notes = "Event date and time")
	private LocalDateTime startDateTime;

	@Future
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm", shape = JsonFormat.Shape.STRING)
	@ApiModelProperty(notes = "Event date and time")
	private LocalDateTime finishDateTime;
	
	@NotNull
	@Positive
	@ApiModelProperty(notes = "Preferred minimum age of the companion")
	private Integer minimumAge;

	@NotNull
	@Positive
	@ApiModelProperty(notes = "Preferred maximum age of the companion")
	private Integer maximumAge;

	@NotNull
	@Column(length = 1)
	@ApiModelProperty(notes = "Preferred event gender of the companion")
	@Enumerated(EnumType.STRING)
	private Gender companionGender;

	@NotEmpty
	@Column(length = 300)
	@Size(min = 1, max = 300)
	@ApiModelProperty(notes = "Additional info for the event")
	private String additionalInfo;

	@NotNull
	@Enumerated(EnumType.STRING)
	protected DataState dataState;

	@PreRemove
	public void deleteUser() {
		this.dataState = DataState.DELETED;
	}
}
