package com.digitinarytask.customer.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging execution of service and repository.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut that matches all repositories.
     */
    @Pointcut("execution(* com.digitinarytask.customer.repository.*.*(..))")
    public void repositoryPointcut() {
    }

    /**
     * Pointcut that matches all services.
     */
    @Pointcut("execution(* com.digitinarytask.customer.service.*.*(..))")
    public void servicePointcut() {
    }

    /**
     * Advice that logs methods before their execution.
     */
    @Before("repositoryPointcut() || servicePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering in Method :  " + joinPoint.getSignature().getName());
        logger.info("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        logger.info("Arguments :  " + Arrays.toString(joinPoint.getArgs()));
        logger.info("Target class : " + joinPoint.getTarget().getClass().getName());
    }

    /**
     * Advice that logs methods after their successful execution.
     */
    @AfterReturning(pointcut = "repositoryPointcut() || servicePointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method Return value : " + result);
        logger.info("Exiting from Method :  " + joinPoint.getSignature().getName());
    }

    /**
     * Advice that logs methods after an exception is thrown.
     */
    @AfterThrowing(pointcut = "repositoryPointcut() || servicePointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
        logger.error("Cause : " + exception.getCause());
    }

    /**
     * Advice that logs methods around their execution.
     */
    @Around("repositoryPointcut() || servicePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            logger.info("Method " + className + "." + methodName + " ()" + " execution time : " + elapsedTime + " ms");
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument " + Arrays.toString(joinPoint.getArgs()) + " in " + joinPoint.getSignature().getName() + "()");
            throw e;
        }
    }
}
