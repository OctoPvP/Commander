package net.octopvp.commander.exception;

import net.octopvp.commander.lang.LocalizedCommandException;
import net.octopvp.commander.lang.ResponseHandler;

public class TestException extends LocalizedCommandException {
    public TestException(ResponseHandler responseHandler) {
        super("message.test", responseHandler);
    }
}
