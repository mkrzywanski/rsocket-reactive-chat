package io.mkrzywanski.chat.app;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

@Component
class StartupListener {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    StartupListener(final ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    void applicationReadyEvent(ApplicationReadyEvent applicationReadyEvent) {
        reactiveMongoTemplate.createCollection(MessageDocument.class);
    }

}
