package com.mymate.mymate.account.dto;

import com.mymate.mymate.account.entity.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "정산 목록 응답", example = """
{
  "accounts": [
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
      "createdBy": 123,
      "participants": [],
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "size": 10,
  "first": true,
  "last": true
}
""")
public class AccountListResponse {

    @Schema(description = "정산 목록")
    private List<AccountResponse> accounts;

    @Schema(description = "전체 요소 수", example = "1")
    private long totalElements;

    @Schema(description = "전체 페이지 수", example = "1")
    private int totalPages;

    @Schema(description = "현재 페이지", example = "0")
    private int currentPage;

    @Schema(description = "페이지 크기", example = "10")
    private int size;

    @Schema(description = "첫 페이지 여부", example = "true")
    private boolean first;

    @Schema(description = "마지막 페이지 여부", example = "true")
    private boolean last;

    public static AccountListResponse from(List<AccountResponse> accounts, long totalElements, int totalPages,
                                           int currentPage, int size, boolean first, boolean last) {
        return AccountListResponse.builder()
                .accounts(accounts)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .size(size)
                .first(first)
                .last(last)
                .build();
    }
}