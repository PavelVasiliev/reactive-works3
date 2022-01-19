package ru.innotech.education.rxjava.config;

import com.zaxxer.hikari.HikariDataSource;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.flywaydb.core.Flyway;
import org.postgresql.Driver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration
public class DatabaseTestConfiguration {
    private static final int PORT = 5432;

    @Container
    private PostgreSQLContainer<?> postgres;

    @Bean
    public PostgreSQLContainer<?> postgres() {
        postgres = MyPostgreSQLContainer.getInstance();
        postgres.start();
        return postgres;
    }

    @DependsOn("postgres")
    @Bean
    public Flyway flyway(){
        Flyway flyway = Flyway
                .configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .load();
        flyway.migrate();
        return flyway;
    }


    @DependsOn("postgres")
    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(postgres.getJdbcUrl());
        dataSource.setUsername(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());
        dataSource.setDriverClassName(Driver.class.getCanonicalName());
        return dataSource;
    }

    @Bean
    @DependsOn({"postgres", "flyway"})
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(postgres.getHost())
                        .port(postgres.getMappedPort(PORT))
                        .database(postgres.getDatabaseName())
                        .username(postgres.getUsername())
                        .password(postgres.getPassword())
                        .build());
    }

    @Bean
    public TransactionManagementConfigurer transactionManagementConfigurer(ReactiveTransactionManager reactiveTransactionManager) {
        return () -> reactiveTransactionManager;
    }
}