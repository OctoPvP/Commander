package net.octopvp.commander.test;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.sender.CoreCommandSender;

public class TestPlatform implements CommanderPlatform {

    @Override
    public void handleMessage(String message, CoreCommandSender sender) {
        System.out.println(message);
    }

    @Override
    public void handleError(String error, CoreCommandSender sender) {
        System.err.println(error);
    }

    @Override
    public void handleCommandException(CommandContext ctx, CommandException e) {
        e.printStackTrace();
    }

    @Override
    public void registerCommand(CommandInfo command) {
        System.out.println("Registered command: " + command.getName());
    }
}
