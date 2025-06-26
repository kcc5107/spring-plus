package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QTodoRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Todo> findByIdWithUser(long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        return Optional.ofNullable(queryFactory.selectFrom(todo)
                .join(todo.user, user)
                .where(todo.id.eq(todoId))
                .fetchOne());
    }
}
