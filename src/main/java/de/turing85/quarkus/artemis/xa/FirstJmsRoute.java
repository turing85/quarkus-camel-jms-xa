package de.turing85.quarkus.artemis.xa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.jms.ConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.jms;

@ApplicationScoped
public class FirstJmsRoute extends AbstractRoute {
  public static final String ROUTE_ID = "FIRST_ROUTE";
  public static final String ROUTE_TOPIC = "FIRST";
  public static final String ROUTE_SUBSCRIPTION_NAME = "FIRST_SUB";

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

  @Override
  public void configure() {
    onException(Exception.class)
        .process(exchange -> stopRoute(exchange, ROUTE_ID));
    from(
        jms("topic:%s".formatted(ROUTE_TOPIC))
            .connectionFactory(connectionFactory)
            .concurrentConsumers(concurrentConsumers)
            .subscriptionDurable(true)
            .subscriptionShared(true)
            .durableSubscriptionName(ROUTE_SUBSCRIPTION_NAME))
        .id(ROUTE_ID)
        .log("Route %s: received \"${body}\"".formatted(ROUTE_ID));
  }
}
