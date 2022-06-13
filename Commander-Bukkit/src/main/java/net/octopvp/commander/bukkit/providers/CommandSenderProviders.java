package net.octopvp.commander.bukkit.providers;

import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.bukkit.impl.BukkitCommandSenderImpl;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.command.CommandSender;

import java.util.Deque;
import java.util.List;

public class CommandSenderProviders {

    public static class CommandSenderProvider implements Provider<CommandSender> {
        @Override
        public CommandSender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return ((BukkitCommandSender) context.getCommandSender()).getSender();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return null;
        }
    }

    public static class CoreCommandSenderProvider implements Provider<CoreCommandSender> {

        @Override
        public CoreCommandSender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return context.getCommandSender();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return null;
        }
    }

    public static class BukkitCoreCommandSenderProvider implements Provider<BukkitCommandSender> {

        @Override
        public BukkitCommandSender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return (BukkitCommandSender) context.getCommandSender();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return null;
        }
    }

    public static class BukkitCoreCommandSenderImplProvider implements Provider<BukkitCommandSenderImpl> {

        @Override
        public BukkitCommandSenderImpl provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return (BukkitCommandSenderImpl) context.getCommandSender();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return null;
        }
    }


}
