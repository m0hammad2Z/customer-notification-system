package com.digitinarytask.notification.event;

import com.digitinarytask.shared.event.NotificationEvent;
import com.digitinarytask.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;


    @KafkaListener(topics = "notification", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        notificationService.handleNotificationEvent(event);
    }

}
