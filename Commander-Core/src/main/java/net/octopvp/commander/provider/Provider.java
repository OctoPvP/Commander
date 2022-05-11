package net.octopvp.commander.provider;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;

import java.util.Deque;
import java.util.List;

public interface Provider<T> {

    T provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args);

    List<String> provideSuggestions(String input);

    default T provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return null;
    }

    default boolean failOnException() {
        return true;
    }


    Class<?> getType();

    default Class<?>[] getExtraTypes() {
        return null;
    }

    default boolean matchWithInstanceOf() {
        return false;
    }
}
