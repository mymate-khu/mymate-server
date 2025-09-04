package com.mymate.mymate.term.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "term",
       uniqueConstraints = {@UniqueConstraint(name = "uk_term_code_version", columnNames = {"code", "version"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Term extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code; // SERVICE, PRIVACY, THIRD_PARTY, AGE_OVER_14, MARKETING

    @Column(nullable = false, length = 20)
    private String version;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentUrl;

    @Column(nullable = false)
    private boolean required;
}


