package net.octopvp.commander.bukkit;

import net.octopvp.commander.bukkit.impl.BukkitCommandWrapper;
import net.octopvp.commander.bukkit.impl.BukkitHelpService;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;

public class BukkitPlatform implements CommanderPlatform {
    private final Plugin plugin;
    private CommandMap commandMap;

    public BukkitPlatform(Plugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (CommandMap) field.get(manager);
            } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleMessage(String message, CoreCommandSender sender) {
        BukkitCommandSender s = (BukkitCommandSender) sender;
        s.sendMessage(message);
    }

    @Override
    public void handleError(String error, CoreCommandSender sender) {
        BukkitCommandSender s = (BukkitCommandSender) sender;
        s.sendMessage(ChatColor.RED + error);
    }

    @Override
    public void handleCommandException(CommandContext ctx, CommandException e) {
        handleError(e.getMessage(), ctx.getCommandSender());
    }

    @Override
    public void handleCommandException(CommandInfo info, CoreCommandSender sender, CommandException e) {
        sender.sendMessage(ChatColor.RED + e.getMessage());
    }

    @Override
    public void registerCommand(CommandInfo command) {
        if (commandMap.getCommand(command.getName()) == null) {
            Command cmd = new BukkitCommandWrapper(command);
            commandMap.register(plugin.getName(), cmd);
        }
    }

    @Override
    public HelpService getHelpService() {
        return BukkitCommander.HELP_SERVICE;
    }
}
