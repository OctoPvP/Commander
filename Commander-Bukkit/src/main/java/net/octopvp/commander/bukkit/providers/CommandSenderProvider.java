package net.octopvp.commander.bukkit.providers;

import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import org.bukkit.command.CommandSender;

import java.util.Deque;
import java.util.List;

public class CommandSenderProvider implements Provider<CommandSender> {
    @Override
    public CommandSender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return ((BukkitCommandSender) context.getCommandSender()).getSender();
    }

    @Override
    public List<String> provideSuggestions(String input) {
        return null;
    }
}
