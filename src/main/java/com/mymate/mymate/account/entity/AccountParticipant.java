package com.mymate.mymate.account.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "account_participant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "member_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @Column(nullable = false)
    private Boolean isPaid = false;

    @Builder
    public AccountParticipant(Long accountId, Long memberId, BigDecimal paymentAmount) {
        this.accountId = accountId;
        this.memberId = memberId;
        this.paymentAmount = paymentAmount;
    }

    public void updatePaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void markAsPaid() {
        this.isPaid = true;
    }

    public void markAsUnpaid() {
        this.isPaid = false;
    }
}