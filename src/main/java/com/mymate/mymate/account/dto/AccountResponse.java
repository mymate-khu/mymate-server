package com.mymate.mymate.account.dto;

import com.mymate.mymate.account.entity.Account;
import com.mymate.mymate.account.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "정산 응답", example = """
{
  "id": 1,
  "title": "저녁 회식비",
  "description": "팀 저녁 회식 비용 정산",
  "expenseDate": "2024-01-15",
  "category": "식비",
  "imageUrl": "https://example.com/receipt.jpg",
  "totalAmount": 120000,
  "receiveAmount": 100000,
  "status": "PENDING",
  "groupId": 1,
  "createdByMemberId": "sw1234",
  "participants": [
    {
      "id": 1,
      "memberId": 123,
      "memberLoginId": "sw1234",
      "memberName": "홍길동",
      "paymentAmount": 30000,
      "isPaid": true
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
""")
public class AccountResponse {

    @Schema(description = "정산 ID", example = "1")
    private Long id;

    @Schema(description = "정산 제목", example = "저녁 회식비")
    private String title;

    @Schema(description = "정산 설명", example = "팀 저녁 회식 비용 정산")
    private String description;

    @Schema(description = "지출 날짜", example = "2024-01-15")
    private LocalDate expenseDate;

    @Schema(description = "지출 카테고리", example = "식비")
    private String category;

    @Schema(description = "영수증 이미지 URL", example = "https://example.com/receipt.jpg")
    private String imageUrl;

    @Schema(description = "총 지출 금액", example = "120000")
    private BigDecimal totalAmount;

    @Schema(description = "정산자가 받을 금액", example = "100000")
    private BigDecimal receiveAmount;

    @Schema(description = "정산 상태", example = "PENDING")
    private AccountStatus status;

    @Schema(description = "그룹 ID", example = "1")
    private Long groupId;

    @Schema(description = "생성자 로그인 아이디", example = "sw1234")
    private String createdByMemberId;

    @Schema(description = "정산 참여자 목록")
    private List<ParticipantResponse> participants;

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    public static AccountResponse from(Account account, List<ParticipantResponse> participants, String createdByMemberId) {
        return AccountResponse.builder()
                .id(account.getId())
                .title(account.getTitle())
                .description(account.getDescription())
                .expenseDate(account.getExpenseDate())
                .category(account.getCategory())
                .imageUrl(account.getImageUrl())
                .totalAmount(account.getTotalAmount())
                .receiveAmount(account.getReceiveAmount())
                .status(account.getStatus())
                .groupId(account.getGroupId())
                .createdByMemberId(createdByMemberId)
                .participants(participants)
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "정산 참여자 정보")
    public static class ParticipantResponse {
        @Schema(description = "참여자 ID", example = "1")
        private Long id;

        @Schema(description = "멤버 ID", example = "123")
        private Long memberId;

        @Schema(description = "멤버 로그인 아이디", example = "sw1234")
        private String memberLoginId;

        @Schema(description = "멤버명", example = "홍길동")
        private String memberName;

        @Schema(description = "지불 금액", example = "30000")
        private BigDecimal paymentAmount;

        @Schema(description = "지불 완료 여부", example = "true")
        private Boolean isPaid;
    }
}