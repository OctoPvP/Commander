package net.octopvp.commander.exception;

import net.octopvp.commander.lang.LocalizedCommandException;

public class CommandNotFoundException extends LocalizedCommandException {
    public CommandNotFoundException(String key, Object... placeholders) {
        super(key, placeholders);
    }

}
