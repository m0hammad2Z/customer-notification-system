package com.digitinarytask.notification.service;

import com.digitinarytask.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncNotificationDispatcher {
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${websocket.topic}")
    private String topic;

    public void dispatchNotification(Notification notification) {
        try {
            log.debug("Dispatching notification to websocket subscribers: {}", notification);
            messagingTemplate.convertAndSend(topic, notification);
        } catch (Exception e) {
            log.error("Error dispatching notification: {}", e.getMessage(), e);
        }
    }
}
