package ru.innotech.education.rxjava.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class MyPostgreSQLContainer extends PostgreSQLContainer<MyPostgreSQLContainer> {
    private static final String IMAGE_VERSION = "postgres:13-alpine";
    private static final String DATABASE_NAME = "rss";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "admin";

    private static MyPostgreSQLContainer container;

    public MyPostgreSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static MyPostgreSQLContainer getInstance() {
        if (container == null) {
            container = new MyPostgreSQLContainer()
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD)
                    .withDatabaseName(DATABASE_NAME);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }

    @Override
    public void stop() {
    }
}