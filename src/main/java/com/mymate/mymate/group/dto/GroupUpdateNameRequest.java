package com.mymate.mymate.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "그룹명 수정 요청", example = """
{
  "name": "새로운 그룹명"
}
""")
public class GroupUpdateNameRequest {

    @NotBlank(message = "그룹명은 필수입니다")
    @Size(max = 50, message = "그룹명은 50자를 초과할 수 없습니다")
    @Schema(description = "새로운 그룹명", example = "새로운 그룹명")
    private String name;

    public GroupUpdateNameRequest(String name) {
        this.name = name;
    }
}
