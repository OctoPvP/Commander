package net.octopvp.commander.lang;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.exception.CommandException;

@Getter
@Setter
public class LocalizedCommandException extends CommandException {
    private final String key;
    private final ResponseHandler responseHandler;

    public LocalizedCommandException(String key, ResponseHandler responseHandler) {
        this.key = key;
        this.responseHandler = responseHandler;
    }

    @Override
    public String getLocalizedMessage() {
        return responseHandler.getMessage(this);
    }
}
