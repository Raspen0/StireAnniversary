package nl.raspen0.stireanniversary.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.raspen0.stireanniversary.StireAnniversary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {
 
    private final StireAnniversary plugin;
    private HikariDataSource dataSource;
 
    private String hostname;
    private String port;
    private String database;
    private String username;
    private String password;
 
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;
 
    public ConnectionPoolManager(StireAnniversary plugin) {
        this.plugin = plugin;
        init();
        setupPool();
    }
 
    private void init() {
        hostname = plugin.getConfig().getString("SQL.host");
        port = plugin.getConfig().getString("SQL.port");
        database = plugin.getConfig().getString("SQL.database");
        username = plugin.getConfig().getString("SQL.username");
        password = plugin.getConfig().getString("SQL.password");
        minimumConnections = plugin.getConfig().getInt("SQL.minimumConnections");
        maximumConnections = plugin.getConfig().getInt("SQL.maximumConnections");
        connectionTimeout = plugin.getConfig().getInt("SQL.connectionTimeout");
    }
 
    private void setupPool() {
        //For some reason I can't catch the SQLException that HikariCP throws.
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        dataSource = new HikariDataSource(config);
    }
 
    Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
 
    void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        if (res != null) try { res.close(); } catch (SQLException ignored) {}
    }
 
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
 
}
        