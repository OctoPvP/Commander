package net.octopvp.commander.bukkit.providers;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.bukkit.impl.BukkitCommandSenderImpl;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

public class PlayerProvider implements Provider<Player> {
    @Getter @Setter
    private static Function<Player, String> playerNameFunction = player -> player.getName();

    @Override
    public Player provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return parameterInfo.getParameter().isAnnotationPresent(Sender.class) || parameterInfo.getParameter().getName().equalsIgnoreCase("sender") ? (Player) ((BukkitCommandSender) context.getCommandSender()).getSender() : Bukkit.getPlayer(args.pop());
    }

    @Override
    public List<String> provideSuggestions(String input, CoreCommandSender s) {
        List<String> list = new ArrayList<>();
        BukkitCommandSenderImpl sender = (BukkitCommandSenderImpl) s;
        if (sender instanceof Player) {
            Player p = sender.getPlayer();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (p.canSee(onlinePlayer)) {
                    if (onlinePlayer.getName().toLowerCase().startsWith(input.toLowerCase())) {
                        list.add(playerNameFunction.apply(onlinePlayer));
                    }
                }
            }
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                list.add(playerNameFunction.apply(onlinePlayer));
            }
        }

        return list;
    }
}
