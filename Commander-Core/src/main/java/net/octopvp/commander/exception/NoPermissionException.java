package net.octopvp.commander.exception;

public class NoPermissionException extends CommandException {
    public NoPermissionException() {
        super("You do not have permission to perform this command.");
    }

    public NoPermissionException(String message) {
        super(message);
    }
}
