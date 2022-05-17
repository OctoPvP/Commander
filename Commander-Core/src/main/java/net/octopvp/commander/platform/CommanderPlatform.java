package net.octopvp.commander.platform;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.UUID;

public interface CommanderPlatform {
    void handleMessage(String message, CoreCommandSender sender);

    void handleError(String error, CoreCommandSender sender);

    void handleCommandException(CommandContext ctx, CommandException e);

    void handleCommandException(CommandInfo info, CoreCommandSender sender, CommandException e);

    default boolean hasPermission(CoreCommandSender sender, String permission){
        return sender.hasPermission(permission);
    }

    void registerCommand(CommandInfo command);

    default String getPrefix(){
        return "/";
    }

    HelpService getHelpService();
}
