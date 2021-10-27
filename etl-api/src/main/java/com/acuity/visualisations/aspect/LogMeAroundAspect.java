package com.acuity.visualisations.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogMeAroundAspect {

    @Around("@annotation(LogMeAround)")
    public Object logMearoundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String classAndMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
        String value = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(LogMeAround.class).value();
        try {
            log.debug("{} method {} has been called", value, classAndMethod);
            Object result = joinPoint.proceed();
            log.debug("{} method {} has been successfully finished", value, classAndMethod);
            return result;
        } catch (Throwable ex) {
            log.error("{} method {} has thrown an exception: {}", value, classAndMethod, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
