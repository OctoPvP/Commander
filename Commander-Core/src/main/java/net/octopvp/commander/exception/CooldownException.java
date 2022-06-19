package net.octopvp.commander.exception;

import net.octopvp.commander.lang.LocalizedCommandException;

import java.text.DecimalFormat;

public class CooldownException extends LocalizedCommandException {
    private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");

    public CooldownException(double timeLeft) {
        super("cooldown", TWO_DECIMAL_FORMAT.format(timeLeft));
        //super("You must wait " + TWO_DECIMAL_FORMAT.format(timeLeft) + " seconds before using this command again.");
    }
}
