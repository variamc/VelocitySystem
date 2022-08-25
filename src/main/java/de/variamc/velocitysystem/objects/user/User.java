package de.variamc.velocitysystem.objects.user;

import com.velocitypowered.api.proxy.Player;
import de.variamc.velocitysystem.VelocitySystem;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Class created by Kaseax on 2022
 */
public class User {

    private final String name;
    private final UUID uniqueId;
    private final Player player;

    public User(String name, UUID uniqueId) {
        if (!VelocitySystem.getInstance().getUsers().contains(this)) {
            VelocitySystem.getInstance().getUsers().add(this);
        }
        this.name = name;
        this.uniqueId = uniqueId;
        this.player = VelocitySystem.getInstance().getServer().getPlayer(this.uniqueId).orElseGet(new Supplier<Player>() {
            @Override
            public Player get() {
                return null;
            }
        });
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Player getPlayer() {
        return player;
    }
}
