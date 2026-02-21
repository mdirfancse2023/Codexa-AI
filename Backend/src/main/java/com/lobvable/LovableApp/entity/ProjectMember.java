package com.lobvable.LovableApp.entity;

import com.lobvable.LovableApp.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_members")
public class ProjectMember {
    @EmbeddedId
    ProjectMemberId id;

    @ManyToOne
    @MapsId("projectId") // mapsId to projectId in ProjectMemberId
    Project project;

    @ManyToOne
    @MapsId("userId") // mapsId to userId in ProjectMemberId
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ProjectRole projectRole;

    Instant invitedAt;
    Instant acceptedAt;
}
