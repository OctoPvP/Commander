package net.octopvp.commander.provider.impl;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class BooleanProvider implements Provider<Boolean> {
    @Override
    public Boolean provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        String arg = args.poll().toLowerCase();
        return Boolean.parseBoolean(arg);
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return Arrays.asList("true", "false");
    }

    @Override
    public Boolean provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return false;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class[]{boolean.class};
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
