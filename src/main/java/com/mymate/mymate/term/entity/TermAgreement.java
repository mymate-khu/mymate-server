package com.mymate.mymate.term.entity;

import java.time.LocalDateTime;

import com.mymate.mymate.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = true)
    private LocalDateTime agreedAt;

    private LocalDateTime withdrawnAt;
}


