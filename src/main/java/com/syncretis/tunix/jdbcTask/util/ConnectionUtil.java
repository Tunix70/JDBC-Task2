package com.syncretis.tunix.jdbcTask.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionUtil {
    private ConnectionUtil connectionUtil = null;
    private final String DATABASE_URL = "jdbc:postgresql://localhost:5432/JDBC_Task";
    private final String USER = "postgres";
    private final String PASS = "root";
    private Connection connection;

    public ConnectionUtil() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (Objects.isNull(connectionUtil)) {
            connectionUtil = new ConnectionUtil();
        }
        return connection;
    }

    public PreparedStatement getPreparedStatement(String SQL) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement(SQL);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return preparedStatement;
    }

}
