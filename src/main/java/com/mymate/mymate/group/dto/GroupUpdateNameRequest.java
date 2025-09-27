package com.mymate.mymate.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupUpdateNameRequest {

    @NotBlank(message = "그룹명은 필수입니다")
    @Size(max = 50, message = "그룹명은 50자를 초과할 수 없습니다")
    private String name;

    public GroupUpdateNameRequest(String name) {
        this.name = name;
    }
}
