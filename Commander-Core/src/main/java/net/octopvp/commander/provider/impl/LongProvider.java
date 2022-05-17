package net.octopvp.commander.provider.impl;

import net.octopvp.commander.annotation.Range;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.provider.Provider;

import java.util.Deque;
import java.util.List;

public class LongProvider implements Provider<Long> {
    @Override
    public Long provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        try {
            return Long.parseLong(args.poll());
        } catch (NumberFormatException e) {
            throw new InvalidArgsException(commandInfo);
        }
    }

    @Override
    public List<String> provideSuggestions(String input) {
        return null;
    }

    @Override
    public Long provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getParameter().isAnnotationPresent(Range.class)) return (long) parameterInfo.getParameter().getAnnotation(Range.class).defaultValue();
        return -1L;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class<?>[]{long.class};
    }

    @Override
    public boolean failOnException() {
        return true;
    }

    @Override
    public boolean failOnExceptionIgnoreOptional() {
        return true;
    }
}
