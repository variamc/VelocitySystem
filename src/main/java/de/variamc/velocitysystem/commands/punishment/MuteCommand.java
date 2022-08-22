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
import java.util.function.Supplier;

/**
 * Class created by Kaseax on 2022
 */
public class MuteCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("mute").requires(commandSource -> commandSource.hasPermission("variamc.team.mute")).executes(this::sendHelp)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string()).suggests(this::setPlayersSuggestions).executes(this::sendHelp)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Insult").executes(context -> mutePlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Insult", 1210000))))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Behavior").executes(context -> mutePlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Behavior", 1210000)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Spamming").executes(context -> mutePlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Spamming", 1210000)))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelp(CommandContext<CommandSource> context) {
        VeloPlayer veloPlayer = new VeloPlayer(((Player) context.getSource()).getUniqueId());
        var mm = MiniMessage.miniMessage();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        Component parsedMessage = mm.deserialize(resourceBundle.getString("mute_help_message"));

        context.getSource().sendMessage(parsedMessage);
        return 1;
    }

    private CompletableFuture<Suggestions> setPlayersSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            suggestionsBuilder.suggest(player.getUsername());
        });
        return suggestionsBuilder.buildFuture();
    }

    private static int mutePlayer(Player player, UUID target, String reason, int duration) {
        VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
        var mm = MiniMessage.miniMessage();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));

        if (target == player.getUniqueId()) return 1;

        if (VelocitySystem.getInstance().getPunishManager().isMuted(target)) {
            Component parsedMessage = mm.deserialize(resourceBundle.getString("mute_is_already_muted_message"));
            player.sendMessage(parsedMessage);
            return 1;
        }

        Player targetPlayer = VelocitySystem.getInstance().getServer().getPlayer(target).orElseGet(new Supplier<Player>() {
            @Override
            public Player get() {
                return null;
            }
        });

        if (targetPlayer != null) {
            if (targetPlayer.hasPermission("variamc.team.cantbemuted")) {
                Component parsedMessage = mm.deserialize(resourceBundle.getString("mute_cant_player_message"));
                player.sendMessage(parsedMessage);
                return 1;
            }

            Component parsedMessage = mm.deserialize(resourceBundle.getString("mute_player_message"),
                    Placeholder.component("time", Component.text(VelocitySystem.getInstance().getPunishManager().getRemainingTime(duration))),
                    Placeholder.component("reason", Component.text(reason)));

            targetPlayer.sendMessage(parsedMessage);
        }

        VelocitySystem.getInstance().getPunishManager().mutePlayer(target, UUIDFetcher.getName(target), reason, duration, player);
        return 1;
    }
}
