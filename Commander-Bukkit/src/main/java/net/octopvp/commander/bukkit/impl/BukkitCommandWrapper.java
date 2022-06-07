package net.octopvp.commander.bukkit.impl;

import net.octopvp.commander.command.CommandInfo;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class BukkitCommandWrapper extends org.bukkit.command.Command {
    private CommandInfo commandInfo;

    public BukkitCommandWrapper(CommandInfo command) {
        super(command.getName(), command.getDescription(), command.getUsage(), Arrays.asList(command.getAliases()));
        this.commandInfo = command;
    }

    //strip the plugin:command from the command if it exists
    private static final Pattern PLUGIN_PREFIX_PATTERN = Pattern.compile("^(?:[a-zA-Z0-9_]*:)?([a-zA-Z0-9_]+)");
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        String label = PLUGIN_PREFIX_PATTERN.matcher(commandLabel).replaceFirst("$1");
        BukkitCommandSenderImpl cmdSenderImpl = new BukkitCommandSenderImpl(sender);
        commandInfo.getCommander().executeCommand(cmdSenderImpl, label, args);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return commandInfo.getCommander().getSuggestions(new BukkitCommandSenderImpl(sender), alias, args);
    }
}
