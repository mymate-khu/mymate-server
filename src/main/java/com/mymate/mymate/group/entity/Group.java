package com.mymate.mymate.group.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Builder
    public Group(String name, Long ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
