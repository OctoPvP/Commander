package net.octopvp.commander.provider.impl;

import net.octopvp.commander.annotation.Range;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.Deque;
import java.util.List;

public class DoubleProvider implements Provider<Double> {
    @Override
    public Double provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        try {
            String arg = args.poll();
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new InvalidArgsException(commandInfo);
        }
    }

    @Override
    public List<String> provideSuggestions(String input, CoreCommandSender sender) {
        return null;
    }

    @Override
    public Double provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getParameter().isAnnotationPresent(Range.class)) return parameterInfo.getParameter().getAnnotation(Range.class).defaultValue();
        return -1d;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class[]{double.class};
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
