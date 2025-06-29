package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoResponse;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.dto.response.QUserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QTodoRepository {

    private final JPAQueryFactory queryFactory;

    public TodoResponse findByIdWithUser(long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

//        return Optional.ofNullable(queryFactory.selectFrom(todo)
//                .join(todo.user, user).fetchJoin()
//                .where(todo.id.eq(todoId))
//                .fetchOne());

        return queryFactory
                .select(new QTodoResponse(
                        todo.id,
                        todo.title,
                        todo.contents,
                        todo.weather,
                        new QUserResponse(user.id, user.email),
                        todo.createdAt,
                        todo.modifiedAt
                ))
                .from(todo)
                .join(todo.user, user)
                .where(todo.id.eq(todoId))
                .fetchOne();
    }

    public Page<TodoSearchResponse> findByFilters(String keyword, LocalDate startTime, LocalDate endTime, String managerNickname, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            booleanBuilder.and(todo.title.containsIgnoreCase(keyword));
        }
        if (startTime != null) {
            booleanBuilder.and(todo.createdAt.goe(startTime.atStartOfDay()));
        }
        if (endTime != null) {
            booleanBuilder.and(todo.createdAt.lt(endTime.plusDays(1).atStartOfDay()));
        }
        if (managerNickname != null && !managerNickname.isEmpty()) {
            booleanBuilder.and(user.nickname.containsIgnoreCase(managerNickname));
        }

        List<TodoSearchResponse> results = queryFactory
                .select(new QTodoSearchResponse(
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.comments, comment)
                .join(todo.managers, manager)
                .join(manager.user, user)
//                .where(todo.title.containsIgnoreCase(keyword)
//                        .and(todo.createdAt.between(startTime, endTime)
//                                .and(user.nickname.containsIgnoreCase(managerNickname))))
                .where(booleanBuilder)
                .groupBy(todo.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.id.countDistinct())
                .from(todo)
                .leftJoin(todo.comments, comment)
                .join(todo.managers, manager)
                .join(manager.user, user)
//                .where(todo.title.containsIgnoreCase(keyword)
//                        .and(todo.createdAt.between(startTime, endTime)
//                                .and(user.nickname.containsIgnoreCase(managerNickname))))
                .where(booleanBuilder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
