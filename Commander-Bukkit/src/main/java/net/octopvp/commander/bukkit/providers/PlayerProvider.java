/*
 * Copyright (c) Badbird5907 2022.
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.octopvp.commander.bukkit.providers;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.annotation.Sender;
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
        if (commandInfo.getCommander().getPlatform().isSenderParameter(parameterInfo)) return (Player) ((BukkitCommandSender) context.getCommandSender()).getSender();
        if (args.size() == 0) {
            if (parameterInfo.getParameter().isAnnotationPresent(DefaultSelf.class))
                return ((BukkitCommandSender) context.getCommandSender()).getPlayer();
            return null;
        }
        return Bukkit.getPlayer(args.pop());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender s) {
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
