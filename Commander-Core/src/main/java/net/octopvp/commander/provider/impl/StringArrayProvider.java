package net.octopvp.commander.provider.impl;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;

import java.util.Deque;
import java.util.List;

public class StringArrayProvider implements Provider<String[]> {
    @Override
    public String[] provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        String[] array = args.toArray(new String[args.size()]);
        args.clear();
        return array;
    }

    @Override
    public List<String> provideSuggestions(String input) {
        return null;
    }
}