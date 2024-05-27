package org.example.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.util.DatabaseProperty;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;


public class PostgreSqlDb implements Database {
    private static final Logger logger = LoggerFactory.getLogger(PostgreSqlDb.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        try {
            config.setJdbcUrl(DatabaseProperty.getDbUrl());
            config.setUsername(DatabaseProperty.getDbUser());
            config.setPassword(DatabaseProperty.getDbPassword());
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(30000);
            ds = new HikariDataSource( config );

            Flyway flyway = Flyway.configure()
                    .dataSource(ds)
                    .locations("db/migrations")
                    .load();
            flyway.migrate();

            logger.info("HikariCP initialized and Flywell executed successfully");
        } catch (FlywayException e) {
            logger.info("Failed to initialize HikariCP DataSource or execute Flyway migrations.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private PostgreSqlDb() {
    }

    public Connection getConnection() throws SQLException {
            return ds.getConnection();
    }

    private static final class InstanceHolder {
        private static final PostgreSqlDb instance = new PostgreSqlDb();
    }

    public static PostgreSqlDb getInstance() {
        return InstanceHolder.instance;
    }
}
