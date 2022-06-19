package net.octopvp.commander.bukkit;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.bukkit.annotation.ConsoleOnly;
import net.octopvp.commander.bukkit.annotation.OpOnly;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.commander.bukkit.impl.BukkitCommandSenderImpl;
import net.octopvp.commander.bukkit.impl.BukkitHelpService;
import net.octopvp.commander.bukkit.providers.CommandSenderProviders;
import net.octopvp.commander.bukkit.providers.OfflinePlayerProvider;
import net.octopvp.commander.bukkit.providers.PlayerProvider;
import net.octopvp.commander.lang.LocalizedCommandException;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ResourceBundle;

public class BukkitCommander {
    public static BukkitHelpService HELP_SERVICE = BukkitHelpService.INSTANCE;
    public static Commander getCommander(Plugin plugin) {
        Commander impl = new CommanderImpl(new BukkitPlatform(plugin))
                .init()
                .registerProvider(Player.class, new PlayerProvider())
                .registerProvider(OfflinePlayer.class, new OfflinePlayerProvider())
                .registerProvider(CommandSender.class, new CommandSenderProviders.CommandSenderProvider())
                .registerProvider(BukkitCommandSender.class, new CommandSenderProviders.BukkitCoreCommandSenderProvider())
                .registerProvider(BukkitCommandSenderImpl.class, new CommandSenderProviders.BukkitCoreCommandSenderImplProvider())
                .registerProvider(CoreCommandSender.class, new CommandSenderProviders.CoreCommandSenderProvider())
                .registerCommandPreProcessor(ctx -> {
                    if (ctx.getCommandInfo().isAnnotationPresent(PlayerOnly.class)) {
                        BukkitCommandSender sender = (BukkitCommandSender) ctx.getCommandSender();
                        if (!sender.isPlayer()) {
                            throw new LocalizedCommandException("must-be.player");
                        }
                    }else if (ctx.getCommandInfo().isAnnotationPresent(ConsoleOnly.class)) {
                        BukkitCommandSender sender = (BukkitCommandSender) ctx.getCommandSender();
                        if (!sender.isConsole()) {
                            throw new LocalizedCommandException("must-be.console");
                        }
                    } else if (ctx.getCommandInfo().isAnnotationPresent(OpOnly.class)) {
                        BukkitCommandSender sender = (BukkitCommandSender) ctx.getCommandSender();
                        if (!sender.isOp()) {
                            throw new LocalizedCommandException("must-be.op");
                        }
                    }
                });
        impl.getResponseHandler().addBundle(ResourceBundle.getBundle("bukkit", impl.getResponseHandler().getLocale()));
        return impl;
    }
}
