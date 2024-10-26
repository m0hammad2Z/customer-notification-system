package com.digitinarytask.shared.event;

import com.digitinarytask.shared.enumeration.NotificationType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationEvent implements Serializable {
    private String eventId;
    private NotificationType type;
    private String entityType;
    private Long entityId;
    private String title;
    private String payload;
    private LocalDateTime timestamp;

    public NotificationEvent() {}

    @JsonCreator
    public NotificationEvent(@JsonProperty("eventId") String eventId,
                             @JsonProperty("type") NotificationType type,
                             @JsonProperty("entityType") String entityType,
                             @JsonProperty("entityId") Long entityId,
                             @JsonProperty("title") String title,
                             @JsonProperty("payload") String payload,
                             @JsonProperty("timestamp") LocalDateTime timestamp) {
        this.eventId = eventId;
        this.type = type;
        this.entityType = entityType;
        this.entityId = entityId;
        this.payload = payload;
        this.title = title;
        this.timestamp = timestamp;
    }
}
