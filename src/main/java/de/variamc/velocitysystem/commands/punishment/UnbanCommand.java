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
public class UnbanCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("unban").requires(commandSource -> commandSource.hasPermission("variamc.team.unban"))
                .executes(this::sendHelp)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string())
                        .suggests(this::setBannedPlayersSuggestions)
                        .executes(context -> unban((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)))))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelp(CommandContext<CommandSource> context) {
        VeloPlayer veloPlayer = new VeloPlayer(((Player) context.getSource()).getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();

        Component parsedMessage = mm.deserialize(resourceBundle.getString("unban_help_message"));
        context.getSource().sendMessage(parsedMessage);

        return 1;
    }

    private CompletableFuture<Suggestions> setBannedPlayersSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getPunishManager().getBannedPlayerData().forEach(suggestionsBuilder::suggest);
        return suggestionsBuilder.buildFuture();
    }

    private int unban(Player player, UUID target) {
        if (VelocitySystem.getInstance().getPunishManager().isBanned(target)) {
            VelocitySystem.getInstance().getPunishManager().unbanPlayer(target, player);
            return 1;
        }

        VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();

        Component parsedMessage = mm.deserialize(resourceBundle.getString("unban_player_is_not_banned_message"));
        player.sendMessage(parsedMessage);

        return 1;
    }
}
