package de.variamc.velocitysystem.manager;

import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import de.variamc.velocitysystem.VelocitySystem;
import de.variamc.velocitysystem.player.VeloPlayer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class created by Kaseax on 2022
 */
@Getter
public class TeamChatManager {

    private final List<Player> notifiedPlayerList;

    public TeamChatManager() {
        this.notifiedPlayerList = new ArrayList<>();
    }

    public void sendTeamChatMessage(PlayerChatEvent playerChatEvent) {
        final String prefix = "§8[§c§lTeamchat§8] §7";
        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            if (player.hasPermission("variamc.team") && notifiedPlayerList.contains(player)) {
                final TextComponent message = Component.text()
                        .content(prefix)
                        .append(Component.text().content(playerChatEvent.getPlayer().getUsername()).color(NamedTextColor.DARK_RED).build())
                        .append(Component.text(" (")).color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(playerChatEvent.getPlayer().getCurrentServer().get().getServerInfo().getName()).color(NamedTextColor.YELLOW))
                        .append(Component.text(") ").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(" ->").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(playerChatEvent.getMessage().replace("@Team", ""))
                        ).build();
                player.sendMessage(message);
            }
        });
    }
}
