package com.lobvable.LovableApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatSession {

    @EmbeddedId
    ChatSessionId id;

    //@ManyToOne
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("projectId") // mapsId to projectId in ChatSessionId
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    Project project;

    //@ManyToOne
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId") // mapsId to projectId in ChatSessionId
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt; // Soft delete timestamp
}
