package de.turing85.quarkus.artemis.xa;

import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.ConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SecondJmsRoute extends AbstractRoute {
  public static final String ID = "SECOND_ROUTE";
  public static final String TOPIC = "SECOND";
  public static final String SUBSCRIPTION_NAME = "SECOND_SUB";

  private final ConnectionFactory connectionFactory;
  private final int concurrentConsumers;

  public SecondJmsRoute(
      @Identifier("second")
      @SuppressWarnings("CdiInjectionPointsInspection")
      ConnectionFactory connectionFactory,

      @ConfigProperty(name = "application.second-queue.concurrentConsumers")
      int concurrentConsumers) {
    this.connectionFactory = connectionFactory;
    this.concurrentConsumers = concurrentConsumers;
  }

  ConnectionFactory connectionFactory() {
    return connectionFactory;
  }

  String topic() {
    return TOPIC;
  }

  String routeId() {
    return ID;
  }

  String subscriptionName() {
    return SUBSCRIPTION_NAME;
  }

  int concurrentConsumers() {
    return concurrentConsumers;
  }
}
