package com.library.librarymanagement.config;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static BasicDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            InputStream inputStream = DatabaseConfig.class.getClassLoader()
                    .getResourceAsStream("database.properties");

            if (inputStream == null) {
                throw new RuntimeException("Unable to find database.properties");
            }

            props.load(inputStream);

            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(props.getProperty("db.driver"));
            dataSource.setUrl(props.getProperty("db.url"));
            dataSource.setUsername(props.getProperty("db.username"));
            dataSource.setPassword(props.getProperty("db.password"));
            dataSource.setInitialSize(Integer.parseInt(props.getProperty("db.initialSize")));
            dataSource.setMaxTotal(Integer.parseInt(props.getProperty("db.maxTotal")));
            dataSource.setMaxIdle(Integer.parseInt(props.getProperty("db.maxIdle")));
            dataSource.setMinIdle(Integer.parseInt(props.getProperty("db.minIdle")));

            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error loading database configuration", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        try {
            if (dataSource != null) {
                dataSource.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
