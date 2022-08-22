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
import org.apache.commons.lang3.LocaleUtils;

import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Class created by Kaseax on 2022
 */
public class UnmuteCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("unmute").requires(commandSource -> commandSource.hasPermission("variamc.team.unmute"))
                .executes(this::sendHelp)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string())
                        .suggests(this::setMutedPlayersSuggestions)
                        .executes(context -> unmute((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)))))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelp(CommandContext<CommandSource> context) {
        VeloPlayer veloPlayer = new VeloPlayer(((Player) context.getSource()).getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();

        Component parsedMessage = mm.deserialize(resourceBundle.getString("unmute_help_message"));
        context.getSource().sendMessage(parsedMessage);

        return 1;
    }

    private CompletableFuture<Suggestions> setMutedPlayersSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getPunishManager().getMutedPlayerData().forEach(suggestionsBuilder::suggest);
        return suggestionsBuilder.buildFuture();
    }

    private int unmute(Player player, UUID target) {
        if (VelocitySystem.getInstance().getPunishManager().isMuted(target)) {
            VelocitySystem.getInstance().getPunishManager().unmutePlayer(target, player);
            return 1;
        }

        VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();

        Component parsedMessage = mm.deserialize(resourceBundle.getString("unmute_player_is_not_muted_message"));
        player.sendMessage(parsedMessage);

        return 1;
    }
}

