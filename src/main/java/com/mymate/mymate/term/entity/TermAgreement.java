package com.mymate.mymate.term.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "term_agreement",
       uniqueConstraints = {@UniqueConstraint(name = "uk_term_agreement_member_term", columnNames = {"memberId", "termId"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long termId;

    @Column(nullable = false)
    private boolean agreed;

    @Column(nullable = false)
    private LocalDateTime agreedAt;

    private LocalDateTime withdrawnAt;
}


