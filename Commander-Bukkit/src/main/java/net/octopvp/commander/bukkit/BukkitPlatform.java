package net.octopvp.commander.bukkit;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.sender.CoreCommandSender;

public class BukkitPlatform implements CommanderPlatform {
    @Override
    public void handleMessage(String message, CoreCommandSender sender) {

    }

    @Override
    public void handleError(String error, CoreCommandSender sender) {

    }

    @Override
    public void handleCommandException(CommandContext ctx, CommandException e) {

    }
}
