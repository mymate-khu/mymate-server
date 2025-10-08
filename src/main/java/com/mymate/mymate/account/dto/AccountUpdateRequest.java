package com.mymate.mymate.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "정산 수정 요청", example = """
{
  "title": "저녁 회식비 (수정)",
  "description": "팀 저녁 회식 비용 정산 - 업데이트",
  "expenseDate": "2024-01-16",
  "category": "회식비",
  "imageUrl": "https://example.com/receipt_updated.jpg",
  "totalAmount": 130000,
  "receiveAmount": 110000,
  "participantIds": [1, 2, 3]
}
""")
public class AccountUpdateRequest {

    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    @Schema(description = "정산 제목", example = "저녁 회식비 (수정)")
    private String title;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    @Schema(description = "정산 설명", example = "팀 저녁 회식 비용 정산 - 업데이트")
    private String description;

    @Schema(description = "지출 날짜", example = "2024-01-16")
    private LocalDate expenseDate;

    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다")
    @Schema(description = "지출 카테고리", example = "회식비")
    private String category;

    @Schema(description = "영수증 이미지 URL", example = "https://example.com/receipt_updated.jpg")
    private String imageUrl;

    @Schema(description = "총 지출 금액", example = "130000")
    private BigDecimal totalAmount;

    @Schema(description = "정산자가 받을 금액", example = "110000")
    private BigDecimal receiveAmount;

    @Schema(description = "정산 참여자 ID 목록", example = "[1, 2, 3]")
    private List<Long> participantIds;
}