package net.octopvp.commander.validator;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.ParameterInfo;

public interface Validator<T> {
    void validate(T value, ParameterInfo parameter, CommandContext context);
}
