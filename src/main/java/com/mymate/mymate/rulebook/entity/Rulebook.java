package com.mymate.mymate.rulebook.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rulebook")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rulebook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long createdBy; // 작성자 ID

    @Builder
    public Rulebook(String title, String content, Long groupId, Long createdBy) {
        this.title = title;
        this.content = content;
        this.groupId = groupId;
        this.createdBy = createdBy;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}