package net.octopvp.commander.exception;

import net.octopvp.commander.command.CommandInfo;

public class InvalidArgsException extends CommandException {
    public InvalidArgsException(CommandInfo info) {
        super("Usage: " + info.getFullUsage());
    }

    public InvalidArgsException(String usage) {
        super("Usage: " + usage);
    }
}
