package com.digitinarytask.customer.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {


    // Monitor performance of all methods in service, repository, and controller packages
    @Around(
        "execution(* com.digitinarytask.customer.service..*(..)) || " +
        "execution(* com.digitinarytask.customer.repository..*(..)) || " +
        "execution(* com.digitinarytask.customer.controller..*(..))"
    )
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        log.info("Method {} executed in {} ms", methodName, executionTime);

        return result;
    }
}
