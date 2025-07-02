package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "log")
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private boolean success;

    @Column(nullable = false)
    private String httpMethod;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String requestUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Log(Long userId, boolean success, String httpMethod, String method, String requestUrl, LocalDateTime createdAt) {
        this.userId = userId;
        this.success = success;
        this.httpMethod = httpMethod;
        this.method = method;
        this.requestUrl = requestUrl;
        this.createdAt = createdAt;
    }
}
