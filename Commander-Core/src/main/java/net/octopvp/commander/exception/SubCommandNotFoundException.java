package net.octopvp.commander.exception;

public class SubCommandNotFoundException extends CommandException {
    public SubCommandNotFoundException(String command) {
        super("Sub command not found: " + command);
    }
}
