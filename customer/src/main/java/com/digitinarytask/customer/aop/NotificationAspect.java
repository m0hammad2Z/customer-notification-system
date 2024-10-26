package com.digitinarytask.customer.aop;

import com.digitinarytask.shared.annotation.Notifiable;
import com.digitinarytask.shared.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Around("@annotation(notifiable)")
    public Object handleNotification(ProceedingJoinPoint joinPoint, Notifiable notifiable) throws Throwable {
        Object result = joinPoint.proceed();

        if (result != null) {
            Long entityId = extractEntityId(result);

            String title = Objects.nonNull(notifiable.title()) && !notifiable.title().isEmpty() ? notifiable.title() : notifiable.type().getTitle();
            String payload = Objects.nonNull(notifiable.message()) && !notifiable.message().isEmpty() ? notifiable.message() : notifiable.type().getMessage() + " with id " + entityId;
            NotificationEvent event = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .type(notifiable.type())
                .entityType(notifiable.entityType())
                .entityId(entityId)
                .title(title)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();

            kafkaTemplate.send("notification", event);
        }

        return result;
    }
    private Long extractEntityId(Object object) throws NoSuchMethodException {
        Method getIdMethod = object.getClass().getMethod("getId");
        try {
            return (Long) getIdMethod.invoke(object);
        } catch (Exception e) {
            throw new IllegalArgumentException("Entity must have id field");
        }
    }
}
