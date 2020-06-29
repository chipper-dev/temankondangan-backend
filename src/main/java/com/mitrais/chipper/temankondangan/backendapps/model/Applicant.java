package com.mitrais.chipper.temankondangan.backendapps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mitrais.chipper.temankondangan.backendapps.model.common.Auditable;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
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
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "applicants")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdBy", "createdDate", "modifiedBy", "modifiedDate" }, allowGetters = true)
@ApiModel(description = "All details about Applicant. ")
@SQLDelete(sql = "UPDATE applicants SET data_state = 'DELETED' WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "data_state <> 'DELETED'")
public class Applicant extends Auditable<String>{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applicant_id_seq_gen")
    @SequenceGenerator(name = "applicant_id_seq_gen", sequenceName = "applicant_id_seq", allocationSize = 1)
    @ApiModelProperty(notes = "Applicant DB id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Where(clause = "data_state <> 'DELETED'")
    private User applicantUser;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @Where(clause = "data_state <> 'DELETED'")
    private Event event;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ApplicantStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected DataState dataState;

    @PreRemove
    public void deleteApplicant() {
        this.dataState = DataState.DELETED;
    }
}
