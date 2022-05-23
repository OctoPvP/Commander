package net.octopvp.commander.bukkit.providers;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Deque;
import java.util.List;

public class OfflinePlayerProvider implements Provider<OfflinePlayer> {
    @Override
    public OfflinePlayer provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return Bukkit.getOfflinePlayer(args.poll());
    }

    @Override
    public List<String> provideSuggestions(String input, CoreCommandSender sender) {
        return null;
    }
}
