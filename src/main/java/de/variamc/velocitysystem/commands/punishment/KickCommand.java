package de.variamc.velocitysystem.commands.punishment;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.variamc.velocitysystem.VelocitySystem;
import de.variamc.velocitysystem.player.VeloPlayer;
import de.variamc.velocitysystem.utils.uuid.UUIDFetcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.LocaleUtils;

import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Class created by Kaseax on 2022
 */
public class KickCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("kick")
                .requires(commandSource -> commandSource.hasPermission("variamc.team.kick")).executes(this::sendHelp)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string()).suggests(this::suggestPlayers).executes(this::sendHelp)
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("kick_reason", StringArgumentType.greedyString()).executes(context -> kickPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), context.getArgument("kick_reason", String.class)))))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelp(CommandContext<CommandSource> context) {
        VeloPlayer veloPlayer = new VeloPlayer(((Player) context.getSource()).getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();
        Component parsedMessage = mm.deserialize("kick_help_message");
        context.getSource().sendMessage(parsedMessage);
        return 1;
    }

    private int kickPlayer(Player player, UUID target, String reason) {
        VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();

        if (VelocitySystem.getInstance().getServer().getPlayer(target).get() == null) return 1;

        Player targetPlayer = VelocitySystem.getInstance().getServer().getPlayer(target).get();

        if(targetPlayer.hasPermission("variamc.team.cantbekicked")) {
            Component parsedMessage = mm.deserialize("kick_cant_player_message");
            player.sendMessage(parsedMessage);
            return 1;
        }

        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(players -> {
            if (players.hasPermission("variamc.team.kick")) {
                Component parsedMessage = mm.deserialize("kick_player_message",
                        Placeholder.component("player", Component.text(targetPlayer.getUsername())),
                        Placeholder.component("kicker", Component.text(player.getUsername())),
                        Placeholder.component("reason", Component.text(reason)));
                player.sendMessage(parsedMessage);
            }
        });

        Component parsedMessage = mm.deserialize("kick_screen",
                Placeholder.component("reason", Component.text(reason)));
        targetPlayer.disconnect(parsedMessage);
        return 1;
    }

    private CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            suggestionsBuilder.suggest(player.getUsername());
        });
        return suggestionsBuilder.buildFuture();
    }


}
