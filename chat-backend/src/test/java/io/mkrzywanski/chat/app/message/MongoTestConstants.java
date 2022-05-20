package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.ContainerCommandWaitStrategy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MongoTestConstants {

    public static final String BITNAMI_MONGODB_IMAGE = "bitnami/mongodb:5.0.7";
    public static final String USERNAME = "user";
    public static final String PASSWORD = "password";
    public static final String DATABASE = "db";

    public static final ContainerCommandWaitStrategy WAIT_STRATEGY = ContainerCommandWaitStrategy.builder()
            .command("mongo", "--quiet", "--port", "27017", "-u", "root", "-p", "password", "--eval", "rs.status().ok")
            .expectedOutput("1\n")
            .build();
}
