package net.octopvp.commander.exception;

import net.octopvp.commander.lang.LocalizedCommandException;

public class NoPermissionException extends LocalizedCommandException {
    public NoPermissionException() {
        super("no-permission");
    }
}
