package de.variamc.velocitysystem.player;

import de.variamc.velocitysystem.VelocitySystem;
import de.variamc.velocitysystem.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Class created by Kaseax on 2022
 */
public class VeloPlayer {

    protected final UUID uuid;

    public VeloPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public String getLanguage() {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT language FROM players WHERE uuid = ?");
            Stmt.setString(1, uuid.toString());
            ResultSet rs = Stmt.executeQuery();
            while (rs.next())
                return rs.getString("language");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
