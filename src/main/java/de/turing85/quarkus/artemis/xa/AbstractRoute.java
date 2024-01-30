package de.turing85.quarkus.artemis.xa;

import jakarta.jms.ConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.RouteController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.jms;

abstract class AbstractRoute extends RouteBuilder {
  private final ExecutorService executorService;

  protected AbstractRoute() {
    this.executorService = Executors.newFixedThreadPool(1);
  }

  abstract String routeId();
  abstract ConnectionFactory connectionFactory();
  abstract String queue();
  abstract int concurrentConsumers();

  @Override
  public final void configure() {
    onException(Exception.class)
        .process(exchange -> stopRoute(exchange, routeId()));
    from(
        jms("queue:%s".formatted(queue()))
            .connectionFactory(connectionFactory())
            .concurrentConsumers(concurrentConsumers()))
        .id(routeId())
        .log("Route %s: received \"${body}\"".formatted(routeId()));
  }

  protected final void stopRoute(Exchange exchange, String routeName) {
    RouteController routeController = exchange
        .getContext()
        .getRouteController();
    ServiceStatus status = routeController
        .getRouteStatus(routeName);
    if (!status.isStopping() && !status.isStopped()) {
      executorService.execute(() -> {
        while (true) {
          try {
            routeController.stopRoute(routeName);
            break;
          } catch (Exception e) {
            // empty on purpose
          }
        }
      });
    }
  }
}
