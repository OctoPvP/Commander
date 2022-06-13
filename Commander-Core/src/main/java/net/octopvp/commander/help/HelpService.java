package net.octopvp.commander.help;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.sender.CoreCommandSender;

public interface HelpService {
    void sendHelp(CommandContext ctx, CoreCommandSender sender);

    void sendHelp(CommandInfo info, CoreCommandSender sender);
}
