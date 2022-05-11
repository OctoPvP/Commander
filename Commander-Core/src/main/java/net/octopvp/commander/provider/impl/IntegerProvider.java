package net.octopvp.commander.provider.impl;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;

import java.util.Deque;
import java.util.List;

public class IntegerProvider implements Provider<Integer> {
    @Override
    public Integer provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        String arg = args.poll();
        assert arg != null;
        return Integer.parseInt(arg);
    }

    @Override
    public List<String> provideSuggestions(String input) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class[]{int.class};
    }
}
