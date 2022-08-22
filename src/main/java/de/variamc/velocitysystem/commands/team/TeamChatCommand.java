package de.variamc.velocitysystem.commands.team;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.variamc.velocitysystem.VelocitySystem;
import de.variamc.velocitysystem.manager.TeamChatManager;
import de.variamc.velocitysystem.player.VeloPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.LocaleUtils;

import java.util.ResourceBundle;


/**
 * Class created by Kaseax on 2022
 */
public class TeamChatCommand {

    private final String prefix = "§8[§c§lTeamchat§8] §7";

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("teamchat").requires(commandSource -> commandSource.hasPermission("variamc.teamchat"))
                .executes(this::sendTeamChatHelp)
                .then(LiteralArgumentBuilder.<CommandSource>literal("list")
                        .executes(this::sendTeamList))
                .then(LiteralArgumentBuilder.<CommandSource>literal("notify")
                        .executes(this::setTeamChatNotify))
                .build();
        return new BrigadierCommand(node);
    }

    private int sendTeamChatHelp(CommandContext<CommandSource> context) {
        VeloPlayer veloPlayer = new VeloPlayer(((Player) context.getSource()).getUniqueId());
        var mm = MiniMessage.miniMessage();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        Component parsedMessage = mm.deserialize(resourceBundle.getString("teamchat_messages"));
        context.getSource().sendMessage(parsedMessage);

        return 1;
    }

    private int sendTeamList(CommandContext<CommandSource> context) {
        VeloPlayer veloPlayer = new VeloPlayer(((Player) context.getSource()).getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();
        Component parsedListMessage = mm.deserialize(resourceBundle.getString("teamchat_list"));

        context.getSource().sendMessage(Component.text("§8§m                                                 "));
        context.getSource().sendMessage(Component.text("§2 "));
        context.getSource().sendMessage(parsedListMessage);
        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            Component parsedListPlayerMessage = mm.deserialize(resourceBundle.getString("teamchat_list_players"), Placeholder.component("player", Component.text(player.getUsername())), Placeholder.component("server", Component.text(player.getCurrentServer().get().getServerInfo().getName())));
            if (player.hasPermission("variamc.team"))
                context.getSource().sendMessage(parsedListPlayerMessage);
        });
        context.getSource().sendMessage(Component.text("§2 "));
        context.getSource().sendMessage(Component.text("§8§m                                                 "));
        return 1;
    }

    private int setTeamChatNotify(CommandContext<CommandSource> context) {
        Component parsedNotifyAdd = null;
        if (VelocitySystem.getInstance().getTeamChatManager().getNotifiedPlayerList().contains((Player) context.getSource())) {
            VelocitySystem.getInstance().getTeamChatManager().getNotifiedPlayerList().remove((Player) context.getSource());
            VeloPlayer veloPlayer = new VeloPlayer(((Player) context.getSource()).getUniqueId());
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
            var mm = MiniMessage.miniMessage();
            Component parsedNotifyRemove = mm.deserialize(resourceBundle.getString("teamchat_notify_remove"));
            parsedNotifyAdd = mm.deserialize(resourceBundle.getString("teamchat_notify_add"));

            context.getSource().sendMessage(parsedNotifyRemove);
        } else {
            VelocitySystem.getInstance().getTeamChatManager().getNotifiedPlayerList().add((Player) context.getSource());
            context.getSource().sendMessage(parsedNotifyAdd);
        }
        return 1;
    }
}
