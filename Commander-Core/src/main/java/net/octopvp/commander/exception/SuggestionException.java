package net.octopvp.commander.exception;

public class SuggestionException extends CommandException {
    public SuggestionException(String message) {
        super(message);
    }

    public SuggestionException(String message, Throwable cause) {
        super(message, cause);
    }
}
