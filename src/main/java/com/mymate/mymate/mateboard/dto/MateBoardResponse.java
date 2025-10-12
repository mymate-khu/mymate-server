package com.mymate.mymate.mateboard.dto;

import com.mymate.mymate.mateboard.entity.MateBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "메이트보드 응답", example = """
{
  "id": 1,
  "memberId": 123,
  "memberName": "홍길동",
  "content": "오늘 하루도 화이팅! 모두 건강하게 보내세요 😊",
  "createdAt": "2024-01-15T10:30:00",
  "expiresAt": "2024-01-16T10:30:00",
  "isOwner": true
}
""")
public class MateBoardResponse {

    @Schema(description = "메이트보드 ID", example = "1")
    private Long id;

    @Schema(description = "작성자 ID", example = "123")
    private Long memberId;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String memberName;

    @Schema(description = "메이트보드 내용", example = "오늘 하루도 화이팅! 모두 건강하게 보내세요 😊")
    private String content;

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "만료일시", example = "2024-01-16T10:30:00")
    private LocalDateTime expiresAt;

    @Schema(description = "작성자 여부", example = "true")
    private boolean isOwner;

    public MateBoardResponse(MateBoard mateBoard, String memberName, boolean isOwner) {
        this.id = mateBoard.getId();
        this.memberId = mateBoard.getMemberId();
        this.memberName = memberName;
        this.content = mateBoard.getContent();
        this.createdAt = mateBoard.getCreatedAt();
        this.expiresAt = mateBoard.getExpiresAt();
        this.isOwner = isOwner;
    }
}