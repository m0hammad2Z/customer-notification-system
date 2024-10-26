package com.digitinarytask.shared.annotation;

import com.digitinarytask.shared.enumeration.NotificationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Notifiable {
    NotificationType type();
    String entityType() default "CUSTOMER";
    String message() default "";
    String title() default "";
}
