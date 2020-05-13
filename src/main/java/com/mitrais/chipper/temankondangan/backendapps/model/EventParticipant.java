package com.mitrais.chipper.temankondangan.backendapps.model;

import java.time.LocalDateTime;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Table(name = "event_participant")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdBy", "createdDate", "modifiedBy", "modifiedDate" }, allowGetters = true)
@ApiModel(description = "All details about Event Participant. ")
@SQLDelete(sql = "UPDATE event_participant SET data_state = 'DELETED' WHERE event_participant_id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "data_state <> 'DELETED'")
public class EventParticipant extends Auditable<String> {

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_participant_id_seq_gen")
	@SequenceGenerator(name = "event_participant_id_seq_gen", sequenceName = "event_participant_id_seq", allocationSize = 1)
	@ApiModelProperty(notes = "Event Participant DB id")
	private Long eventParticipantId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;

	@NotNull
	@Enumerated(EnumType.STRING)
	protected DataState dataState;

	@PreRemove
	public void deleteUser() {
		this.dataState = DataState.DELETED;
	}
}
