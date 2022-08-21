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
public class BanCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("ban").requires(commandSource -> commandSource.hasPermission("variamc.team.ban")).executes(BanCommand::sendHelp)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string()).suggests(this::setPlayersSuggestions).executes(BanCommand::sendHelp)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Hacking").executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Hacking", 604800))
                                .then(LiteralArgumentBuilder.<CommandSource>literal("Drohungen").executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Drohungen", 2419000))))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Cape/Skin/Name").executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Cape/Skin/Name", 2419000)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Teaming").executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Teaming", 604800)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Hacking-Bestätigung").requires(commandSource -> commandSource.hasPermission("variamc.team.ban.higher")).executes(context -> overwriteBan((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Hacking", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Todeswunsch").requires(commandSource -> commandSource.hasPermission("variamc.team.ban.higher")).executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Todeswunsch", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Bannumgehung").requires(commandSource -> commandSource.hasPermission("variamc.team.ban.higher")).executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Bannumgehung", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Diskriminierung").requires(commandSource -> commandSource.hasPermission("variamc.team.ban.higher")).executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Diskriminierung", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Sicherheitsban").requires(commandSource -> commandSource.hasPermission("variamc.team.ban.reason.security")).executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Sicherheitsban", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Hausverbot").requires(commandSource -> commandSource.hasPermission("variamc.team.ban.reason.house_ban")).executes(context -> banPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)), "Hausverbot", -1)))
                ).build();

        return new BrigadierCommand(node);
    }

    private CompletableFuture<Suggestions> setPlayersSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            suggestionsBuilder.suggest(player.getUsername());
        });
        return suggestionsBuilder.buildFuture();
    }

    private static int sendHelp(CommandContext<CommandSource> context) {
        VeloPlayer veloPlayer = new VeloPlayer(((Player) context.getSource()).getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();
        Component parsedMessage = mm.deserialize(resourceBundle.getString("ban_help_message"));

        context.getSource().sendMessage(parsedMessage);
        return 1;
    }

    private static int banPlayer(Player player, UUID target, String reason, int duration) {
        VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();
        if (target == player.getUniqueId()) return 1;

        if(VelocitySystem.getInstance().getPunishManager().isBanned(target)) {
            Component parsedMessage = mm.deserialize(resourceBundle.getString("ban_is_already_banned_message"));
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
            if (targetPlayer.hasPermission("variamc.team.cannotbanned")) {
                Component parsedMessage = mm.deserialize(resourceBundle.getString("ban_cant_player_message"));
                return 1;
            }

            Component parsedMessage = mm.deserialize(resourceBundle.getString("ban_screen_message"),
                    Placeholder.component("reason", Component.text(VelocitySystem.getInstance().getPunishManager().getBanReasonFromUUID(targetPlayer.getUniqueId()))),
                    Placeholder.component("time", Component.text(VelocitySystem.getInstance().getPunishManager().getRemainingTime(duration))));
            player.disconnect(parsedMessage);
        }

        VelocitySystem.getInstance().getPunishManager().banPlayer(target, UUIDFetcher.getName(target), reason, duration, player);
        return 1;
    }

    private static int overwriteBan(Player player, UUID target, String reason, int duration) {
        VelocitySystem.getInstance().getPunishManager().unbanPlayer(target, player);
        banPlayer(player, target, reason, duration);

        return 1;
    }
}
