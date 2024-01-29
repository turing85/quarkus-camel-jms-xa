package de.turing85.quarkus.artemis.xa;

import org.apache.camel.Exchange;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.RouteController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract class AbstractRoute extends RouteBuilder {
  private final ExecutorService executorService;

  protected AbstractRoute() {
    this.executorService = Executors.newFixedThreadPool(1);
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
