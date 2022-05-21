package net.octopvp.commander.bukkit;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.bukkit.annotation.ConsoleOnly;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.commander.bukkit.impl.BukkitHelpService;
import net.octopvp.commander.bukkit.providers.CommandSenderProvider;
import net.octopvp.commander.bukkit.providers.PlayerProvider;
import net.octopvp.commander.exception.CommandParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BukkitCommander {
    public static BukkitHelpService HELP_SERVICE = BukkitHelpService.INSTANCE;
    public static Commander getCommander(Plugin plugin) {
        return new CommanderImpl(new BukkitPlatform(plugin))
                .init()
                .registerProvider(Player.class,new PlayerProvider())
                .registerProvider(CommandSender.class, new CommandSenderProvider())
                .registerCommandPreProcessor(ctx -> {
                    if (ctx.getCommandInfo().isAnnotationPresent(PlayerOnly.class)) {
                        BukkitCommandSender sender = (BukkitCommandSender) ctx.getCommandSender();
                        if (!sender.isPlayer()) {
                            throw new CommandParseException("You must be a player to use this command.");
                        }
                    }else if (ctx.getCommandInfo().isAnnotationPresent(ConsoleOnly.class)) {
                        BukkitCommandSender sender = (BukkitCommandSender) ctx.getCommandSender();
                        if (!sender.isConsole()) {
                            throw new CommandParseException("You must be a console to use this command.");
                        }
                    }
                })
                ;
    }
}
