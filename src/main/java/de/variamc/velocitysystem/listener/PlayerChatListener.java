package de.variamc.velocitysystem.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import de.variamc.velocitysystem.VelocitySystem;
import de.variamc.velocitysystem.player.VeloPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.LocaleUtils;

import java.util.ResourceBundle;

/**
 * Class created by Kaseax on 2022
 */
public class PlayerChatListener {

    @Subscribe
    public void handlePlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();

        if (player.hasPermission("variamc.teamchat") && event.getMessage().startsWith("@Team ")) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            if (!VelocitySystem.getInstance().getTeamChatManager().getNotifiedPlayerList().contains(player)) {
                Component parsedMessage = mm.deserialize(resourceBundle.getString("teamchat_loggedout"));
                player.sendMessage(parsedMessage);
                return;
            }
            VelocitySystem.getInstance().getTeamChatManager().sendTeamChatMessage(event);
            return;
        } else if (player.hasPermission("variamc.teamchat") && event.getMessage().startsWith("@Team")) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            if (!VelocitySystem.getInstance().getTeamChatManager().getNotifiedPlayerList().contains(player)) {
                Component parsedMessage = mm.deserialize(resourceBundle.getString("teamchat_error"));
                player.sendMessage(parsedMessage);
                return;
            }

            if (VelocitySystem.getInstance().getPunishManager().isMuted(player.getUniqueId())) {
                long current = System.currentTimeMillis();
                long end = VelocitySystem.getInstance().getPunishManager().getMuteEnding(player.getUniqueId());
                if (((current < end ? 1 : 0) | (end == -1L ? 1 : 0)) != 0) {
                    Component parsedMessage = mm.deserialize(resourceBundle.getString("mute_player_message"),
                            Placeholder.component("time", Component.text(VelocitySystem.getInstance().getPunishManager().getRemainingTime(VelocitySystem.getInstance().getPunishManager().getMuteEnding(player.getUniqueId())))),
                            Placeholder.component("reason", Component.text(VelocitySystem.getInstance().getPunishManager().getMuteReasonFromUUID(player.getUniqueId()))));
                    player.sendMessage(parsedMessage);
                    event.setResult(PlayerChatEvent.ChatResult.denied());
                } else {
                    VelocitySystem.getInstance().getPunishManager().unmutePlayer(player.getUniqueId(), null);
                }
            }
        }
    }
}
