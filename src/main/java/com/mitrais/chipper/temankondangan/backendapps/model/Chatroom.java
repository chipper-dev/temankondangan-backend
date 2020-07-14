package com.mitrais.chipper.temankondangan.backendapps.model;

import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chatroom")
@SQLDelete(sql = "UPDATE chatroom SET data_state = 'DELETED' WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "data_state <> 'DELETED'")
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @Where(clause = "data_state <> 'DELETED'")
    @Audited(targetAuditMode = NOT_AUDITED)
    private Event event;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected DataState dataState;
}
