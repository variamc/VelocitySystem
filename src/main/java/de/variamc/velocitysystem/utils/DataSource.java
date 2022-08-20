package de.variamc.velocitysystem.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.variamc.velocitysystem.VelocitySystem;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class created by Kaseax on 2022
 */
public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:mysql://" + VelocitySystem.getInstance().getConfigManager().getConfig().getString("mysql.host") + ":" + VelocitySystem.getInstance().getConfigManager().getConfig().getString("mysql.port") + "/" + VelocitySystem.getInstance().getConfigManager().getConfig().getString("mysql.database"));
        config.setUsername(VelocitySystem.getInstance().getConfigManager().getConfig().getString("mysql.user"));
        config.setPassword(VelocitySystem.getInstance().getConfigManager().getConfig().getString("mysql.password"));
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
