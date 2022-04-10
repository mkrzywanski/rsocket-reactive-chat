package io.mkrzywanski.chat;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.testcontainers.containers.GenericContainer;

@Configuration
class MongoConfig {

    @Autowired
    protected Environment environment;

    private static final String USERNAME = "userrrr";
    private static final String PASSWORD = "password";
    private static final String DATABASE = "dbaa";

    @Bean
    GenericContainer<?> mongoDBContainer() {
        var mongoReplicaSetReady = ContainerCommandWaitStrategy.builder()
                .command("mongo", "--quiet", "--port", "27017", "-u", "root", "-p", "password", "--eval", "rs.status().ok")
                .expectedOutput("1\n")
                .build();

        var mongoDBContainer = new GenericContainer<>("bitnami/mongodb:4.4.12")
                .withEnv("MONGODB_USERNAME", USERNAME)
                .withEnv("MONGODB_PASSWORD", PASSWORD)
                .withEnv("MONGODB_DATABASE", DATABASE)
                .withEnv("MONGODB_REPLICA_SET_MODE", "primary")
                .withEnv("MONGODB_REPLICA_SET_KEY", "someKey1")
                .withEnv("MONGODB_ROOT_PASSWORD", "password")
                .waitingFor(mongoReplicaSetReady)
                .withExposedPorts(27017);
        mongoDBContainer.start();
        return mongoDBContainer;
    }

    @Bean
    MongoClientSettingsBuilderCustomizer mongoSettingsCustomizer(final GenericContainer<?> mongoDBContainer) {
        var port = mongoDBContainer.getFirstMappedPort();
        var url = String.format("mongodb://localhost:%s/%s?replicaSet=replicaset&authSource=admin", port, DATABASE);
        var connectionString = new ConnectionString(url);
        return (settings) -> settings.applyConnectionString(connectionString)
                .credential(MongoCredential.createCredential(USERNAME, DATABASE, PASSWORD.toCharArray()));
    }
}
