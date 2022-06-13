package net.octopvp.commander.exception;

public class CommandParseException extends CommandException{
    public CommandParseException(String message) {
        super(message);
    }
    public CommandParseException() {

    }
    public CommandParseException(String message, Throwable cause) {
        super(message, cause);
    }
    public CommandParseException(Throwable cause) {
        super(cause.getMessage());
    }
}
