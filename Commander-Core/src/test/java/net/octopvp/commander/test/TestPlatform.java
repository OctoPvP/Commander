package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.lang.LocalizedCommandException;
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
        if (e instanceof LocalizedCommandException) handleLocale(e, ctx.getCommandInfo().getCommander());
        else e.printStackTrace();
    }

    @Override
    public void handleCommandException(CommandInfo info, CoreCommandSender sender, CommandException e) {
        if (e instanceof LocalizedCommandException) handleLocale(e, info.getCommander());
        else e.printStackTrace();
    }

    public void handleLocale(Exception e, Commander commander) {
        System.err.println(commander.getResponseHandler().getMessage(e));
    }

    @Override
    public void registerCommand(CommandInfo command) {
        //System.out.println("Registered command: " + command.getName());
    }

    @Override
    public HelpService getHelpService() {
        return new HelpService() {
            @Override
            public void sendHelp(CommandContext ctx, CoreCommandSender sender) {
                System.out.println("Help Service 1");
            }

            @Override
            public void sendHelp(CommandInfo info, CoreCommandSender sender) {
                System.out.println("Help Service 2");
            }
        };
    }
}
