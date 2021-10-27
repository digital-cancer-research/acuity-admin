/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
