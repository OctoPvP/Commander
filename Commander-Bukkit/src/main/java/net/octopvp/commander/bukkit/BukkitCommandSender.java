package net.octopvp.commander.bukkit;

import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface BukkitCommandSender extends CoreCommandSender {
    CommandSender getSender();

    boolean isPlayer();

    boolean isConsole();

    Player getPlayer();

    UUID getUUID();
    default UUID getUniqueId() {
        return getUUID();
    }

    String getName();
}
