package io.mkrzywanski.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;
import org.testcontainers.shaded.org.awaitility.Awaitility;


public class ContainerCommandWaitStrategy extends AbstractWaitStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerCommandWaitStrategy.class);

    private final String[] command;
    private final String expectedOutput;

    ContainerCommandWaitStrategy(final String[] command, final String expectedOutput) {
        this.command = command;
        this.expectedOutput = expectedOutput;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void waitUntilReady() {
        LOG.info("Waiting for container " + waitStrategyTarget.getContainerId() + " to be ready");
        Awaitility.await().atMost(startupTimeout).until(() -> {
            final Container.ExecResult result = waitStrategyTarget.execInContainer(command);
            return result.getStdout().equals(expectedOutput);
        });
        LOG.info("Container " + waitStrategyTarget.getContainerId() + " ready");
    }

    public static class Builder {
        private String[] command = {};
        private String expectedOutput = "";

        public Builder command(final String... command) {
            this.command = command;
            return this;
        }

        public Builder expectedOutput(final String expectedOutput) {
            this.expectedOutput = expectedOutput;
            return this;
        }

        public ContainerCommandWaitStrategy build() {
            return new ContainerCommandWaitStrategy(command, expectedOutput);
        }
    }

}
