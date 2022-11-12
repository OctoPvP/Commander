package net.octopvp;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.sender.CoreCommandSender;

public class JDAPlatform implements CommanderPlatform {
    @Override
    public void handleMessage(String message, CoreCommandSender sender) {

    }

    @Override
    public void handleError(String error, CoreCommandSender sender) {

    }

    @Override
    public void handleCommandException(CommandContext ctx, CommandException e) {

    }

    @Override
    public void handleCommandException(CommandInfo info, CoreCommandSender sender, CommandException e) {

    }

    @Override
    public void registerCommand(CommandInfo command) {

    }

    @Override
    public void updateCommandAliases(CommandInfo commandInfo) {

    }

    @Override
    public HelpService getHelpService() {
        return null;
    }

    @Override
    public void runAsync(Runnable runnable) {

    }
}
