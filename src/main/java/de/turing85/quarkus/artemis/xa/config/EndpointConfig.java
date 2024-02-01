package de.turing85.quarkus.artemis.xa.config;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.DefaultTaskExecutorType;
import org.apache.camel.quarkus.core.events.ComponentAddEvent;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

@Dependent
public class EndpointConfig {
  private final PlatformTransactionManager platformTransactionManager;

  public EndpointConfig(
      UserTransaction userTransaction,
      @SuppressWarnings("CdiInjectionPointsInspection") TransactionManager transactionManager) {
    this.platformTransactionManager = new JtaTransactionManager(userTransaction, transactionManager);
  }

  public void onComponentAdd(@Observes ComponentAddEvent event) {
    if (event.getComponent() instanceof JmsComponent jmsComponent) {
      jmsComponent.setTransactionManager(platformTransactionManager);
      jmsComponent.getConfiguration().setSynchronous(true);
      jmsComponent.getConfiguration().setTransacted(true);
      jmsComponent.getConfiguration().setDefaultTaskExecutorType(DefaultTaskExecutorType.ThreadPool);
    }
  }
}
