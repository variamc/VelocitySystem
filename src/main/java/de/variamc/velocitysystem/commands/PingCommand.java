package de.variamc.velocitysystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.variamc.velocitysystem.player.VeloPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.LocaleUtils;

import java.util.ResourceBundle;

/**
 * Class created by Kaseax on 2022
 */
public class PingCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        VeloPlayer veloPlayer = new VeloPlayer(((Player) invocation.source()).getUniqueId());
        var mm = MiniMessage.miniMessage();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        Component parsedMessage = mm.deserialize(resourceBundle.getString("ping_message"),
                Placeholder.component("ping", Component.text((((Player) invocation.source()).getPing()))));
        invocation.source().sendMessage(parsedMessage);
    }
}
