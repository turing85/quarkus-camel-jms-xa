package de.turing85.quarkus.artemis.xa;

import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.ConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.jms;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.log;

@ApplicationScoped
public class SecondJmsRoute extends AbstractRoute {
  public static final String ROUTE_ID = "SECOND_ROUTE";
  public static final String ROUTE_TOPIC = "SECOND";
  public static final String ROUTE_SUBSCRIPTION_NAME = "SECOND_SUB";

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

  @Override
  public void configure() {
    onException(Exception.class)
        .process(exchange -> stopRoute(exchange, ROUTE_ID));
    from(
        jms("topic:%s".formatted(ROUTE_TOPIC))
            .connectionFactory(connectionFactory)
            .concurrentConsumers(concurrentConsumers)
            .subscriptionShared(true)
            .subscriptionShared(true)
            .durableSubscriptionName(ROUTE_SUBSCRIPTION_NAME))
        .id(ROUTE_ID)
        .to(log("Route %s: received \"${body}\"".formatted(ROUTE_ID)));
  }
}
