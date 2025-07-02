package org.example.expert.domain.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 살아 있어서, 리플렉션으로 읽어 쓸 수 있음. 커스텀 어노테이션 사용시 반드시 필요.
public @interface SaveLog {
}