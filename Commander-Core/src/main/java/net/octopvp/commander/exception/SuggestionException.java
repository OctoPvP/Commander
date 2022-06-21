package net.octopvp.commander.exception;

import net.octopvp.commander.lang.LocalizedCommandException;

public class SuggestionException extends LocalizedCommandException {
    public SuggestionException(String key, Object... args) {
        super(key, args);
    }
}
