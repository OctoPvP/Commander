package net.octopvp.commander.exception;

import net.octopvp.commander.lang.LocalizedCommandException;

public class CommandParseException extends LocalizedCommandException {
    public CommandParseException() {
        super("parse.fail");
    }

    public CommandParseException(Exception cause) {
        super("parse.fail", cause);
    }

    public CommandParseException(Object... placeholders) {
        super("parse.fail", placeholders);
    }

    public CommandParseException(String key) {
        super(key);
    }

    public CommandParseException(String key, Object... placeholders) {
        super(key, placeholders);
    }
}
