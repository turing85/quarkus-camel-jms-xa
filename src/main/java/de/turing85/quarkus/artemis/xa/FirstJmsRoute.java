package de.turing85.quarkus.artemis.xa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.jms.ConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FirstJmsRoute extends AbstractRoute {
  public static final String ID = "FIRST_ROUTE";
  public static final String TOPIC = "FIRST";
  public static final String SUBSCRIPTION_NAME = "FIRST_SUB";

  private final ConnectionFactory connectionFactory;
  private final int concurrentConsumers;

  public FirstJmsRoute(
      @Default
      @SuppressWarnings("CdiInjectionPointsInspection")
      ConnectionFactory connectionFactory,

      @ConfigProperty(name = "application.first-queue.concurrentConsumers")
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
