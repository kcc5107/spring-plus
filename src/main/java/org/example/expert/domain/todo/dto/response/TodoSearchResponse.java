package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TodoSearchResponse {

    private final String title;
    private final Long managerCount;
    private final Long totalComments;

    @QueryProjection
    public TodoSearchResponse(String title, Long managerCount, Long totalComments) {
        this.title = title;
        this.managerCount = managerCount;
        this.totalComments = totalComments;
    }
}