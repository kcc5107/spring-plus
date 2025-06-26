package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoResponse;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.dto.response.QUserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QTodoRepository {

    private final JPAQueryFactory queryFactory;

    public TodoResponse findByIdWithUser(long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

//        return Optional.ofNullable(queryFactory.selectFrom(todo)
//                .join(todo.user, user)
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
}
