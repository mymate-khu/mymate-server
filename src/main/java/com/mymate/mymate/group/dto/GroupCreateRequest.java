package com.mymate.mymate.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "그룹 생성 요청", example = """
{
  "name": "우리 가족"
}
""")
public class GroupCreateRequest {

    @NotBlank(message = "그룹명은 필수입니다")
    @Size(max = 50, message = "그룹명은 50자를 초과할 수 없습니다")
    @Schema(description = "그룹명", example = "우리 가족")
    private String name;

    public GroupCreateRequest(String name) {
        this.name = name;
    }
}
