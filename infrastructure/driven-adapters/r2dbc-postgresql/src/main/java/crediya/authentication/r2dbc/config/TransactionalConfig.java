package crediya.authentication.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class TransactionalConfig {

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionPool connectionPool) {
        return new R2dbcTransactionManager(connectionPool);
    }

    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}