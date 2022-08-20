package de.variamc.velocitysystem.manager;

import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import de.variamc.velocitysystem.VelocitySystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Class created by Kaseax on 2022
 */
public class TeamChatManager {

    private final List<Player> notifiedPlayerList;

    public TeamChatManager() {
        this.notifiedPlayerList = new ArrayList<>();
    }

    public void sendTeamChatMessage(PlayerChatEvent playerChatEvent) {
        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            if (player.hasPermission("variamc.team") && notifiedPlayerList.contains(player))
                player.hasPermission("Test");
        });
    }
}
