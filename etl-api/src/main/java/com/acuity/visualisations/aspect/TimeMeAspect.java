package com.acuity.visualisations.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This can time spring beans methods.
 */
@Aspect
@Component
public class TimeMeAspect extends TimeMeLog {
    @Around("within(@com.acuity.visualisations.aspect.TimeMe *) || execution(* com.acuity.visualisations.*.dao.*.*(..))")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        String classAndMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
        logExecutionTime(classAndMethod, stopWatch, Arrays.stream(joinPoint.getArgs()).collect(Collectors.toList()));
        return result;
    }
}
