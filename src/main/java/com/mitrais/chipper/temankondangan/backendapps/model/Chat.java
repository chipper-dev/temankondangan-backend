package com.mitrais.chipper.temankondangan.backendapps.model;

import com.mitrais.chipper.temankondangan.backendapps.model.common.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Size;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat")
public class Chat extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    @Where(clause = "data_state <> 'DELETED'")
    @Audited(targetAuditMode = NOT_AUDITED)
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Where(clause = "data_state <> 'DELETED'")
    @Audited(targetAuditMode = NOT_AUDITED)
    private User user;

    @Size(max = 255)
    private String body;
}
