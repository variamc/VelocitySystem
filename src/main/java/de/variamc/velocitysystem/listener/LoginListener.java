package de.variamc.velocitysystem.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
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
public class LoginListener {

    @Subscribe
    public void handleLogin(LoginEvent event) {
        Player player = event.getPlayer();
        VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();
        if (!VelocitySystem.getInstance().getTeamChatManager().getNotifiedPlayerList().contains(player) && player.hasPermission("variamc.team")) {
            Component parsedMessage = mm.deserialize(resourceBundle.getString("teamchat_loggedout"));
            player.sendMessage(parsedMessage);
        }

        if(VelocitySystem.getInstance().getPunishManager().isBanned(player.getUniqueId())) {
            long current = System.currentTimeMillis();
            long end = VelocitySystem.getInstance().getPunishManager().getBanEnding(player.getUniqueId());
            if (((current < end ? 1 : 0 ) | (end == -1L ? 1 : 0)) != 0) {
                Component parsedMessage = mm.deserialize(resourceBundle.getString("ban_screen_message"),
                        Placeholder.component("reason", Component.text(VelocitySystem.getInstance().getPunishManager().getBanReasonFromUUID(player.getUniqueId()))),
                        Placeholder.component("time", Component.text(VelocitySystem.getInstance().getPunishManager().getRemainingTime(VelocitySystem.getInstance().getPunishManager().getBanEnding(player.getUniqueId())))));
                player.disconnect(parsedMessage);
            } else {
                VelocitySystem.getInstance().getPunishManager().unbanPlayer(player.getUniqueId(), null);
            }
        }
    }
}
