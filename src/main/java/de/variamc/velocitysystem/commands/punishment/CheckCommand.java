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
public class CheckCommand {
    
    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("check").requires(commandSource -> commandSource.hasPermission(" "))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string()).suggests(this::setPlayersSuggestions).executes(context -> checkPlayer((Player) context.getSource(), UUIDFetcher.getUUID(context.getArgument("playername", String.class)))))
                .build();

        return new BrigadierCommand(node);
    }

    private CompletableFuture<Suggestions> setPlayersSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getServer().getAllPlayers().forEach(player -> {
            suggestionsBuilder.suggest(player.getUsername());
        });
        return suggestionsBuilder.buildFuture();
    }

    private int checkPlayer(Player player, UUID target) {
        VeloPlayer veloPlayer = new VeloPlayer(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang.velocitysystem", LocaleUtils.toLocale(veloPlayer.getLanguage()));
        var mm = MiniMessage.miniMessage();
        Component parsedLoadingMessage = mm.deserialize(resourceBundle.getString("check_player_loading_message"));
        player.sendMessage(parsedLoadingMessage);
        if(VelocitySystem.getInstance().getPunishManager().isBanned(target)) {
            Component parsedBannedMessage = mm.deserialize(resourceBundle.getString("check_player_ban_yes_message"),
                    Placeholder.component("player", Component.text(UUIDFetcher.getName(target))),
                    Placeholder.component("banner", Component.text(VelocitySystem.getInstance().getPunishManager().getBannerFromUUID(target))),
                    Placeholder.component("reason", Component.text(VelocitySystem.getInstance().getPunishManager().getBanReasonFromUUID(target))),
                    Placeholder.component("time", Component.text(VelocitySystem.getInstance().getPunishManager().getRemainingTime(VelocitySystem.getInstance().getPunishManager().getBanEnding(target)))));
            player.sendMessage(parsedBannedMessage);
        } else {
            Component parsedNotBannedMessage = mm.deserialize(resourceBundle.getString("check_player_ban_no_message"),
                    Placeholder.component("player", Component.text(UUIDFetcher.getName(target))));
            player.sendMessage(parsedNotBannedMessage);
        }

        if(VelocitySystem.getInstance().getPunishManager().isMuted(target)) {
            Component parsedMuteMessage = mm.deserialize(resourceBundle.getString("check_player_mute_yes_message"),
                    Placeholder.component("player", Component.text(UUIDFetcher.getName(target))),
                    Placeholder.component("muter", Component.text(VelocitySystem.getInstance().getPunishManager().getMuterFromUUID(target))),
                    Placeholder.component("reason", Component.text(VelocitySystem.getInstance().getPunishManager().getMuteReasonFromUUID(target))),
                    Placeholder.component("time", Component.text(VelocitySystem.getInstance().getPunishManager().getRemainingTime(VelocitySystem.getInstance().getPunishManager().getMuteEnding(target)))));
            player.sendMessage(parsedMuteMessage);
        } else {
            Component parsedNotMutedMessage = mm.deserialize(resourceBundle.getString("check_player_mute_no_message"),
                    Placeholder.component("player", Component.text(UUIDFetcher.getName(target))));
            player.sendMessage(parsedNotMutedMessage);
        }
        return 1;
    }


}
