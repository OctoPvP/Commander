package net.octopvp.commander.exception;

public class CooldownException extends CommandException {
    public CooldownException(double timeLeft) {
        super("You must wait " + timeLeft + " seconds before using this command again.");
    }
}
