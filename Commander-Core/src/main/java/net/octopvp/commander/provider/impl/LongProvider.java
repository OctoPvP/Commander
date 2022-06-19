package net.octopvp.commander.provider.impl;

import net.octopvp.commander.annotation.Duration;
import net.octopvp.commander.annotation.Range;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.lang.LocalizedCommandException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.commander.util.CommanderUtilities;

import java.util.Deque;
import java.util.List;

public class LongProvider implements Provider<Long> {
    @Override
    public Long provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        String arg = args.poll();
        if (parameterInfo.getParameter().isAnnotationPresent(Duration.class)) {
            Duration duration = parameterInfo.getParameter().getAnnotation(Duration.class);
            if (arg.equalsIgnoreCase("perm") || arg.equalsIgnoreCase("permanent")) {
                if (!duration.allowPermanent()) {
                    throw new LocalizedCommandException("permanent.not.allowed");
                }
                return -1L;
            }
            return CommanderUtilities.parseTime(arg == null || arg.isEmpty() ? duration.defaultValue() : arg, duration.future());
        }
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            throw new InvalidArgsException(commandInfo);
        }
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return null;
    }

    @Override
    public Long provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getParameter().isAnnotationPresent(Duration.class)) {
            Duration duration = parameterInfo.getParameter().getAnnotation(Duration.class);
            return CommanderUtilities.parseTime(duration.defaultValue(), duration.future());
        }
        if (parameterInfo.getParameter().isAnnotationPresent(Range.class))
            return (long) parameterInfo.getParameter().getAnnotation(Range.class).defaultValue();
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
