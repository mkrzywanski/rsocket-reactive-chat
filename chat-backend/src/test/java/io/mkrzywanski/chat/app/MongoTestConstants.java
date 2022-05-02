package io.mkrzywanski.chat.app;

import io.mkrzywanski.chat.ContainerCommandWaitStrategy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MongoTestConstants {

    static final String BITNAMI_MONGODB_IMAGE = "bitnami/mongodb:5.0.7";
    static final String USERNAME = "user";
    static final String PASSWORD = "password";
    static final String DATABASE = "db";

    static final ContainerCommandWaitStrategy WAIT_STRATEGY = ContainerCommandWaitStrategy.builder()
            .command("mongo", "--quiet", "--port", "27017", "-u", "root", "-p", "password", "--eval", "rs.status().ok")
            .expectedOutput("1\n")
            .build();
}
