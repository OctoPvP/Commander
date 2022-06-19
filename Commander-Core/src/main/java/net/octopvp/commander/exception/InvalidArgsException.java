package net.octopvp.commander.exception;

import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.lang.LocalizedCommandException;

public class InvalidArgsException extends LocalizedCommandException {
    public InvalidArgsException(CommandInfo info) {
        super("args.invalid", info.getFullUsage());
    }

    public InvalidArgsException(String usage) {
        super("args.invalid", usage);
    }
}
