package net.octopvp.commander.provider.impl;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;

import java.util.Deque;
import java.util.List;

public class LongProvider implements Provider<Long> {
    @Override
    public Long provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return Long.parseLong(args.poll());
    }

    @Override
    public List<String> provideSuggestions(String input) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return Long.class;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class<?>[]{long.class};
    }
}
