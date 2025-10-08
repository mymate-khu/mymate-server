package com.mymate.mymate.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "정산 생성 요청", example = """
{
  "title": "저녁 회식비",
  "description": "팀 저녁 회식 비용 정산",
  "expenseDate": "2024-01-15",
  "category": "식비",
  "imageUrl": "https://example.com/receipt.jpg",
  "totalAmount": 120000,
  "receiveAmount": 100000,
  "participantIds": [1, 2, 3, 4]
}
""")
public class AccountCreateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    @Schema(description = "정산 제목", example = "저녁 회식비")
    private String title;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    @Schema(description = "정산 설명", example = "팀 저녁 회식 비용 정산")
    private String description;

    @NotNull(message = "지출 날짜는 필수입니다")
    @Schema(description = "지출 날짜", example = "2024-01-15")
    private LocalDate expenseDate;

    @NotBlank(message = "카테고리는 필수입니다")
    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다")
    @Schema(description = "지출 카테고리", example = "식비")
    private String category;

    @Schema(description = "영수증 이미지 URL", example = "https://example.com/receipt.jpg")
    private String imageUrl;

    @NotNull(message = "총 금액은 필수입니다")
    @Schema(description = "총 지출 금액", example = "120000")
    private BigDecimal totalAmount;

    @NotNull(message = "받을 금액은 필수입니다")
    @Schema(description = "정산자가 받을 금액", example = "100000")
    private BigDecimal receiveAmount;

    @NotNull(message = "참여자 목록은 필수입니다")
    @Size(min = 1, message = "최소 1명의 참여자가 필요합니다")
    @Schema(description = "정산 참여자 ID 목록", example = "[1, 2, 3, 4]")
    private List<Long> participantIds;
}