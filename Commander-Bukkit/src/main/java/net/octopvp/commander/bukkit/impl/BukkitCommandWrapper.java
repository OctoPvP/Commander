package net.octopvp.commander.bukkit.impl;

import net.octopvp.commander.Commander;
import net.octopvp.commander.command.CommandInfo;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class BukkitCommandWrapper extends org.bukkit.command.Command {
    private CommandInfo commandInfo;

    public BukkitCommandWrapper(CommandInfo command) {
        super(command.getName(), command.getDescription(), command.getUsage(), Arrays.asList(command.getAliases()));
        this.commandInfo = command;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        BukkitCommandSenderImpl cmdSenderImpl = new BukkitCommandSenderImpl(sender);
        commandInfo.getCommander().executeCommand(cmdSenderImpl, commandLabel, args);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return commandInfo.getCommander().getSuggestions(new BukkitCommandSenderImpl(sender), alias, args);
    }
}
