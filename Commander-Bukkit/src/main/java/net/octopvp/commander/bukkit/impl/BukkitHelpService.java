package net.octopvp.commander.bukkit.impl;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.ChatColor;

public class BukkitHelpService implements HelpService {
    public static BukkitHelpService INSTANCE = new BukkitHelpService();

    protected BukkitHelpService(){} // We only need one instance of this class

    @Override
    public void sendHelp(CommandContext ctx, CoreCommandSender sender) {
        CommandInfo info = ctx.getCommandInfo();
        sendHelp(info,sender);
    }

    @Override
    public void sendHelp(CommandInfo info, CoreCommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------");
        sender.sendMessage(ChatColor.AQUA + "Help for " + ChatColor.GOLD + info.getName() + ChatColor.GRAY + " - " + info.getDescription());
        if (info.isParentCommand()) {
            for (CommandInfo subCommand : info.getSubCommands()) {
                sender.sendMessage(ChatColor.GRAY + " " + subCommand.getFullUsage() + (subCommand.getDescription() != null || subCommand.getDescription().isEmpty() ? ChatColor.GRAY + " - " + subCommand.getDescription() : ""));
            }
        }
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------");
    }
}
