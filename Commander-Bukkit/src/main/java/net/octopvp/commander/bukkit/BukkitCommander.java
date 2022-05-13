package net.octopvp.commander.bukkit;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.bukkit.providers.CommandSenderProvider;
import net.octopvp.commander.bukkit.providers.PlayerProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BukkitCommander {
    public static Commander getCommander(Plugin plugin) {
        return new CommanderImpl(new BukkitPlatform(plugin))
                .init()
                .registerProvider(Player.class,new PlayerProvider())
                .registerProvider(CommandSender.class, new CommandSenderProvider());
    }
}
