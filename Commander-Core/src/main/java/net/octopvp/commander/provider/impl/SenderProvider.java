package net.octopvp.commander.provider.impl;

import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.Deque;
import java.util.List;

public class SenderProvider implements Provider<CoreCommandSender> {
    @Override
    public CoreCommandSender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return parameterInfo.getParameter().isAnnotationPresent(Sender.class) || parameterInfo.getParameter().getName().equalsIgnoreCase("sender") ? context.getCommandSender() : null;
    }

    @Override
    public List<String> provideSuggestions(String input, CoreCommandSender sender) {
        return null;
    }

    @Override
    public boolean matchWithInstanceOf() {
        return true;
    }
}
