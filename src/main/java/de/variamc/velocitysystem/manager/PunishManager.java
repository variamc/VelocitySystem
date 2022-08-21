package de.variamc.velocitysystem.manager;

import com.velocitypowered.api.proxy.Player;
import de.variamc.velocitysystem.VelocitySystem;
import de.variamc.velocitysystem.player.VeloPlayer;
import de.variamc.velocitysystem.utils.DataSource;
import de.variamc.velocitysystem.utils.uuid.UUIDFetcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.LocaleUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Class created by Kaseax on 2022
 */
public class PunishManager {

    public void banPlayer(UUID uuid, String playerName, String reason, long seconds, Player banner) {
        long end = calculateEnd(seconds);

        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
            var mm = MiniMessage.miniMessage();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
            Component parsedMessage = mm.deserialize(resourceBundle.getString("ban_message"), Placeholder.component("player", Component.text(playerName)),
                    Placeholder.component("banner", Component.text(banner.getUsername())),
                    Placeholder.component("reason", Component.text(reason)),
                    Placeholder.component("time", Component.text(getRemainingTime(end))));

            if(player.hasPermission("variamc.team.ban")) {
                player.sendMessage(parsedMessage);
            }
        });
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("INSERT INTO bannedPlayerData (playerName, playerUuid, time, reason, bannerName) VALUES ('" + playerName + "','" + uuid + "','" + end + "','" + reason + "','" + banner.getUsername() + "')");
            Stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void mutePlayer(UUID uuid, String playerName, String reason, long seconds, Player muter) {
        long end = calculateEnd(seconds);

        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
            var mm = MiniMessage.miniMessage();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
            Component parsedMessage = mm.deserialize(resourceBundle.getString("mute_message"),
                    Placeholder.component("player", Component.text(playerName)),
                    Placeholder.component("muter", Component.text(muter.getUsername())),
                    Placeholder.component("reason", Component.text(reason)),
                    Placeholder.component("time", Component.text(getRemainingTime(end))));

            if (player.hasPermission("variamc.team.mute")) {
                player.sendMessage(parsedMessage);
            }
        });

        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("INSERT INTO mutedPlayerData (playerName, playerUuid, time, reason, muterName) VALUES ('" + playerName + "','" + uuid + "','" + end + "','" + reason + "','" + muter.getUsername() + "')");
            Stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void unbanPlayer(UUID uuid, Player player) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("DELETE FROM bannedPlayerData WHERE playerUuid='" + uuid + "'");
            Stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(players -> {
            VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
            var mm = MiniMessage.miniMessage();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
            Component parsedMessage = mm.deserialize(resourceBundle.getString("unban_message"),
                    Placeholder.component("player", Component.text(UUIDFetcher.getName(uuid))),
                    Placeholder.component("unbanner", Component.text(player != null ? player.getUsername() : "SYSTEM")));
            if (players.hasPermission("variamc.team.ban")) {
                players.sendMessage(parsedMessage);
            }
        });
    }

    public void unmutePlayer(UUID uuid, Player player) {
        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(players -> {
            VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
            var mm = MiniMessage.miniMessage();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
            Component parsedMessage = mm.deserialize(resourceBundle.getString("unmute_message"),
                    Placeholder.component("player", Component.text(UUIDFetcher.getName(uuid))),
                    Placeholder.component("unmuter", Component.text(player != null ? player.getUsername() : "SYSTEM")));
            if (players.hasPermission("variamc.team.mute")) {
                players.sendMessage(parsedMessage);
            }
        });

        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("DELETE FROM mutedPlayerData WHERE playerUuid='" + uuid + "'");
            Stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isBanned(UUID uuid) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT time FROM bannedPlayerData WHERE playerUuid='" + uuid + "'");
            ResultSet rs = Stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMuted(UUID uuid) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT time FROM mutedPlayerData WHERE playerUuid='" + uuid + "'");
            ResultSet rs = Stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getBanReasonFromUUID(UUID uuid) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM bannedPlayerData WHERE playerUuid='" + uuid + "'");
            ResultSet rs = Stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMuteReasonFromUUID(UUID uuid) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM mutedPlayerData WHERE playerUuid='" + uuid + "'");
            ResultSet rs = Stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getBannerFromUUID(UUID uuid) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM bannedPlayerData WHERE playerUuid='" + uuid + "'");
            ResultSet rs = Stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("bannerName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMuterFromUUID(UUID uuid) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM mutedPlayerData WHERE playerUuid='" + uuid + "'");
            ResultSet rs = Stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("muterName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Long getBanEnding(UUID uuid) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM bannedPlayerData WHERE playerUuid='" + uuid + "'");
            ResultSet rs = Stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getMuteEnding(UUID uuid) {
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM mutedPlayerData WHERE playerUuid='" + uuid + "'");
            ResultSet rs = Stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getBannedPlayerDataFromPlayer(String name) {
        ArrayList<String> list = new ArrayList<>();
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM bannedPlayerData WHERE bannerName='" + name + "'");
            ResultSet rs = Stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("bannerName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getMutedPlayerDataFromPlayer(String name) {
        ArrayList<String> list = new ArrayList<>();
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM mutedPlayerData WHERE muterName='" + name + "'");
            ResultSet rs = Stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("muterName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getBannedPlayerData() {
        ArrayList<String> list = new ArrayList<>();
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM bannedPlayerData");
            ResultSet rs = Stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("playerName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getMutedPlayerData() {
        ArrayList<String> list = new ArrayList<>();
        try (Connection con = DataSource.getConnection()) {
            PreparedStatement Stmt = con.prepareStatement("SELECT * FROM mutedPlayerData");
            ResultSet rs = Stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("playerName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private long calculateEnd(long seconds) {
        if (seconds == -1) {
            return -1;
        } else {
            long current = System.currentTimeMillis();
            long millis = seconds * 1000;
            return current + millis;
        }
    }

    public String getRemainingTime(long time) {
        long current = System.currentTimeMillis();
        long end = time;
        if (end == -1) {
            return "§c§lPERMANENT";
        }
        long millis = end - current;
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;
        long weeks = 0;
        while (millis > 1000) {
            millis -= 1000;
            ++seconds;
        }
        while (seconds > 60) {
            seconds -= 60;
            ++minutes;
        }
        while (minutes > 60) {
            minutes -= 60;
            ++hours;
        }
        while (hours > 24) {
            hours -= 24;
            ++days;
        }
        while (days > 7) {
            days -= 7;
            ++weeks;
        }
        return "§4§l" + weeks + " §7Weeks§8, §4§l" + days + " §7Days§8, §4§l" + hours + " §7Hours and §4§l" + minutes + " §7Minutes";
    }
}
