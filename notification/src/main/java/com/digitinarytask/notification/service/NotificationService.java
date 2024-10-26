package com.digitinarytask.notification.service;

import com.digitinarytask.shared.event.NotificationEvent;
import com.digitinarytask.notification.domain.Notification;
import com.digitinarytask.notification.domain.enumeration.NotificationStatus;
import com.digitinarytask.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final AsyncNotificationDispatcher asyncDispatcher;

    private final ConcurrentLinkedQueue<Notification> failedNotifications = new ConcurrentLinkedQueue<>();

    @Async
    @Transactional
    public void handleNotificationEvent(NotificationEvent event) {
        log.debug("Handling notification event: {}", event);
        Notification notification = createNotification(event);
        notificationRepository.save(notification);

        handleDispatch(notification);
    }

    private void handleDispatch(Notification notification) {
        try{
            notification.setStatus(NotificationStatus.IN_PROGRESS);
            notification = notificationRepository.save(notification);

            asyncDispatcher.dispatchNotification(notification);

            notification.setStatus(NotificationStatus.PROCESSED);
            notification.setProcessedAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error dispatching notification: {}", e.getMessage(), e);
            handleNotificationError(notification, e);
        } finally {
            notificationRepository.save(notification);
        }
    }

    @Async
    @Scheduled(fixedRate = 60000)
    public void retryFailedNotifications() {
        log.info("Retrying failed notifications");

        while (!failedNotifications.isEmpty()) {
            Notification notification = failedNotifications.poll();
            handleDispatch(notification);
        }
    }

    private void handleNotificationError(Notification notification, Exception e) {
        notification.setStatus(NotificationStatus.FAILED);
        notification.setErrorMessage(e.getMessage());

        if (notification.getRetryCount() < 3) {
            notification.setStatus(NotificationStatus.RETRY);
            notification.setRetryCount(notification.getRetryCount() + 1);

            log.info("Notification will be retried: {}", notification);
        } else {
            log.error("Notification failed after 3 retries: {}", notification);

            notification.setStatus(NotificationStatus.FAILED);

            notificationRepository.save(notification);
        }
    }

    private Notification createNotification(NotificationEvent event) {
        return Notification.builder()
                .type(event.getType())
                .entityType(event.getEntityType())
                .entityId(event.getEntityId())
                .title(event.getTitle())
                .payload(event.getPayload())
                .createdAt(event.getTimestamp())
                .status(NotificationStatus.PENDING)
                .build();
    }
}
