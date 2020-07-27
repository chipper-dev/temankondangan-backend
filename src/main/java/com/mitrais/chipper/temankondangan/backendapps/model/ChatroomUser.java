package com.mitrais.chipper.temankondangan.backendapps.model;

import com.mitrais.chipper.temankondangan.backendapps.model.common.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chatroom_user")
public class ChatroomUser extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    @Where(clause = "data_state <> 'DELETED'")
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Where(clause = "data_state <> 'DELETED'")
    private User user;


}
