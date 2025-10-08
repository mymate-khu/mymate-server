package com.mymate.mymate.account.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import com.mymate.mymate.account.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal receiveAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.PENDING;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long createdBy; // 정산을 등록한 사용자 ID

    @Builder
    public Account(String title, String description, LocalDate expenseDate, String category,
                   String imageUrl, BigDecimal totalAmount, BigDecimal receiveAmount,
                   Long groupId, Long createdBy) {
        this.title = title;
        this.description = description;
        this.expenseDate = expenseDate;
        this.category = category;
        this.imageUrl = imageUrl;
        this.totalAmount = totalAmount;
        this.receiveAmount = receiveAmount;
        this.groupId = groupId;
        this.createdBy = createdBy;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public void updateCategory(String category) {
        this.category = category;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void updateReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public void complete() {
        this.status = AccountStatus.COMPLETED;
    }

    public void reopen() {
        this.status = AccountStatus.PENDING;
    }
}