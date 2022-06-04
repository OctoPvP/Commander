package net.octopvp.commander.provider.impl;

import net.octopvp.commander.annotation.JoinStrings;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.Deque;
import java.util.List;

public class StringProvider implements Provider<String> {
    @Override
    public String provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getParameter().isAnnotationPresent(JoinStrings.class)) {
            if (args.size() == 0) {
                return null;
            }

            return String.join(" ", args);
        }
        return args.poll();
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return null;
    }
}
