package com.mymate.mymate.rulebook.dto;

import com.mymate.mymate.rulebook.entity.Rulebook;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "룰북 응답", example = """
{
  "id": 1,
  "title": "우리집 생활 규칙",
  "content": "1. 설거지는 자기가 사용한 것은 자기가\\n2. 쓰레기는 분리배출하기\\n3. 공용 공간은 깔끔하게 정리하기",
  "groupId": 1,
  "createdBy": 123,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
""")
public class RulebookResponse {

    @Schema(description = "룰북 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "우리집 생활 규칙")
    private String title;

    @Schema(description = "내용", example = "1. 설거지는 자기가 사용한 것은 자기가\\n2. 쓰레기는 분리배출하기\\n3. 공용 공간은 깔끔하게 정리하기")
    private String content;

    @Schema(description = "그룹 ID", example = "1")
    private Long groupId;

    @Schema(description = "작성자 ID", example = "123")
    private Long createdBy;

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    public RulebookResponse(Rulebook rulebook) {
        this.id = rulebook.getId();
        this.title = rulebook.getTitle();
        this.content = rulebook.getContent();
        this.groupId = rulebook.getGroupId();
        this.createdBy = rulebook.getCreatedBy();
        this.createdAt = rulebook.getCreatedAt();
        this.updatedAt = rulebook.getUpdatedAt();
    }

    public static RulebookResponse from(Rulebook rulebook) {
        return new RulebookResponse(rulebook);
    }
}