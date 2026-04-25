package com.rest.todo.service;

import com.rest.todo.entity.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);

    public void sendTodoCreatedNotification(Todo todo) {
        logger.info("Notification sent for new TODO with id={} and title='{}'", todo.getId(), todo.getTitle());
    }
}

