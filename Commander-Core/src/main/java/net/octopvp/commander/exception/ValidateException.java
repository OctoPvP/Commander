package net.octopvp.commander.exception;

import net.octopvp.commander.lang.LocalizedCommandException;

public class ValidateException extends LocalizedCommandException {
    public ValidateException(String key, Object... placeholders) {
        super(key, placeholders);
    }
}
