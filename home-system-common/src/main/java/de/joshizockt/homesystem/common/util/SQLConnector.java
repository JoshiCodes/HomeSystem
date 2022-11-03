package de.joshizockt.homesystem.common.util;

import java.sql.*;

public class SQLConnector {

    public static final String TABLE_NAME = "homesystem_homes";

    private Connection connection;

    public SQLConnector(String host, String port, String database, String username, String password) throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true";
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Creates the Default Table
     * @throws SQLException
     */
    public void createDefaults() throws SQLException {
        update("CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (`uuid` VARCHAR(300) NOT NULL, `home` VARCHAR(300) NOT NULL, `location` TEXT NOT NULL);");
        // Note:
        //      location is Stored as plain text in the format "World;X;Y;Z;Yaw;Pitch".
        //      Alternative to this would be to use a single row for each value.
    }

    public void update(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    public PreparedStatement prepare(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public ResultSet query(String query) throws SQLException {
        return connection.createStatement().executeQuery(query);
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() throws SQLException {
        if(connection == null || connection.isClosed()) return;
        connection.close();
    }

}
