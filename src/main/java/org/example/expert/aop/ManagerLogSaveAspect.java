package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.dto.CustomUserDetails;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.service.LogService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class ManagerLogSaveAspect {

    private final LogService logService;

    @Around("@annotation(org.example.expert.domain.common.annotation.SaveLog)")
    public Object loggingManagerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // 비로그인 API에도 적용시킬경우 null체크 필요: if (authentication != null)
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String methodName = joinPoint.getSignature().getName();
        Object result = null;
        boolean isSuccess = true;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            isSuccess = false;
            throw e; // 예외를 다시 던지지 않으면 예외가 AOP 안에서 처리되어 API 메서드의 트랜잭션 롤백 등이 작동하지 않음
        } finally {
            Log managerLog = Log.builder()
                    .userId(user.getId())
                    .httpMethod(request.getMethod())
                    .method(methodName)
                    .requestUrl(request.getRequestURI())
                    .success(isSuccess)
                    .createdAt(LocalDateTime.now())
                    .build();

            logService.saveLog(managerLog);
            // Log클래스에 @ToString 사용
            log.info("loggingManagerMethod - {}", managerLog);
        }
    }
}