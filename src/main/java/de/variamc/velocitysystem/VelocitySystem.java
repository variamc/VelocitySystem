package de.variamc.velocitysystem;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.variamc.velocitysystem.commands.PingCommand;
import de.variamc.velocitysystem.commands.punishment.*;
import de.variamc.velocitysystem.commands.team.TeamChatCommand;
import de.variamc.velocitysystem.listener.LoginListener;
import de.variamc.velocitysystem.listener.PlayerChatListener;
import de.variamc.velocitysystem.manager.ConfigManager;
import de.variamc.velocitysystem.manager.PunishManager;
import de.variamc.velocitysystem.manager.TeamChatManager;
import de.variamc.velocitysystem.objects.user.UserCache;
import lombok.Getter;

import java.util.logging.Logger;

/**
 * Class created by Kaseax on 2022
 */
@Plugin(id = "velocitysystem", name = "VelocitySystem", version = "1.0-SNAPSHOT",
        authors = {"VariaMC, Kaseax"})
@Getter
public class VelocitySystem {

    private static VelocitySystem instance;
    private final ProxyServer server;
    private final ConfigManager configManager;
    private final PunishManager punishManager;
    private final TeamChatManager teamChatManager;

    private UserCache users;

    public static String teamChatPrefix = "§8[§c§lTeamchat§8] §7";

    public static final String report_prefix = "§8[§cReport§8] §7";



    @Inject
    public VelocitySystem(ProxyServer server) {
        instance = this;
        this.server = server;
        System.out.println("Trying to init ConfigManager...");
        this.configManager = new ConfigManager();
        System.out.println("Successfully!");

        System.out.println("Trying to init Punishmanager...");
        this.punishManager = new PunishManager();
        System.out.println("Successfully");

        System.out.println("Trying to init TeamChatManager...");
        this.teamChatManager = new TeamChatManager();
        System.out.println("Successfully");

        System.out.println("Trying to init UserCache...");
        this.users = new UserCache();
        System.out.println("Successfully");
        System.out.println("Successfully started.");
    }

    @Subscribe
    public void handleProxyInitialization(ProxyInitializeEvent event) {
        EventManager eventManager = server.getEventManager();
        CommandManager commandManager = server.getCommandManager();

        eventManager.register(this, new LoginListener());
        eventManager.register(this, new PlayerChatListener());
        commandManager.register(commandManager.metaBuilder("ping").build(), new PingCommand());
        commandManager.register(new TeamChatCommand().build());
        commandManager.register(new BanCommand().build());
        commandManager.register(new CheckCommand().build());
        commandManager.register(new KickCommand().build());
        commandManager.register(new MuteCommand().build());
        commandManager.register(new UnbanCommand().build());
        commandManager.register(new UnmuteCommand().build());
    }

    @Subscribe
    public void handleProxyShutdown(ProxyShutdownEvent event) {

    }

    public static VelocitySystem getInstance() {
        return instance;
    }

    public ProxyServer getServer() {
        return server;
    }

}
