package net.octopvp.commander.bukkit.providers;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.bukkit.annotation.DefaultSelf;
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
    @Getter
    @Setter
    private static Function<Player, String> playerNameFunction = player -> player.getName();

    @Override
    public Player provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (args.size() == 0) {
            if (parameterInfo.getParameter().isAnnotationPresent(DefaultSelf.class))
                return ((BukkitCommandSender) context.getCommandSender()).getPlayer();
            return null;
        }
        if (parameterInfo.getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            return (Player) ((BukkitCommandSender) context.getCommandSender()).getSender();
        }
        return Bukkit.getPlayer(args.pop());
    }

    @Override
    public Player provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            return ((BukkitCommandSender) context.getCommandSender()).getPlayer();
        }
        return null;
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender s)  {
        return suggest(s);
    }

    public static List<String> suggest(CoreCommandSender s) {
        List<String> list = new ArrayList<>();
        BukkitCommandSenderImpl sender = (BukkitCommandSenderImpl) s;
        if (sender.isPlayer()) {
            Player p = sender.getPlayer();
            Bukkit.getOnlinePlayers().stream().filter(p::canSee).forEach(onlinePlayer -> list.add(playerNameFunction.apply(onlinePlayer)));
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                list.add(playerNameFunction.apply(onlinePlayer));
            }
        }

        return list;
    }
}
