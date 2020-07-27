package com.mitrais.chipper.temankondangan.backendapps.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat")
public class Chat{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    @JsonBackReference
    @Where(clause = "data_state <> 'DELETED'")
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumn(name = "user_id")    
    @Where(clause = "data_state <> 'DELETED'")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ChatMessage.ContentType contentType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Size(max = 255)
    private String body;
}
