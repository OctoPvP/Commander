package net.octopvp.commander.exception;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CooldownException extends CommandException {
    private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");
    public CooldownException(double timeLeft) {
        super("You must wait " + TWO_DECIMAL_FORMAT.format(timeLeft) + " seconds before using this command again.");
    }
}
