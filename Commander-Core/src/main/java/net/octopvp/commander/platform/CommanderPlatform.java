package net.octopvp.commander.platform;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.sender.CoreCommandSender;

public interface CommanderPlatform {
    void handleMessage(String message, CoreCommandSender sender);

    void handleError(String error, CoreCommandSender sender);

    void handleCommandException(CommandContext ctx, CommandException e);

    default boolean hasPermission(CoreCommandSender sender, String permission){
        return sender.hasPermission(permission);
    }
}
