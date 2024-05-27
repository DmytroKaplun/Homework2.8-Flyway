package org.example.util;

import org.example.database.PostgreSqlDb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseProperty {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = PostgreSqlDb.class.getResourceAsStream("/database.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDbUrl() {
        return PROPERTIES.getProperty("config.url");
    }

    public static String getDbUser() {
        return PROPERTIES.getProperty("config.user");
    }

    public static String getDbPassword() {
        return PROPERTIES.getProperty("config.password");
    }

    public static String initDb() {
        return PROPERTIES.getProperty("sql.init_db");
    }

    public static String populateDb() {
        return PROPERTIES.getProperty("sql.populate_db");
    }
}
