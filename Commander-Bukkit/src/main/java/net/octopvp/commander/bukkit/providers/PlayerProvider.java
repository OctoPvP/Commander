package net.octopvp.commander.bukkit.providers;

import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;

public class PlayerProvider implements Provider<Player> {
    @Override
    public Player provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return parameterInfo.getParameter().isAnnotationPresent(Sender.class) || parameterInfo.getParameter().getName().equalsIgnoreCase("sender") ? (Player) ((BukkitCommandSender) context.getCommandSender()).getSender() : Bukkit.getPlayer(args.pop());
    }

    @Override
    public List<String> provideSuggestions(String input) {
        return null;
    }
}
