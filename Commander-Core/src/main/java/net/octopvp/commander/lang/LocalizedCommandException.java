package net.octopvp.commander.lang;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.exception.CommandException;

@Getter
@Setter
public class LocalizedCommandException extends CommandException {
    private final String key;
    private ResponseHandler responseHandler;

    public LocalizedCommandException(String key, ResponseHandler responseHandler) {
        this.key = key;
        this.responseHandler = responseHandler;
    }

    public LocalizedCommandException(String key) {
        this(key, null);
    }

    public static void checkResponseHandlerNull(Exception e, ResponseHandler responseHandler) {
        if (e instanceof LocalizedCommandException) {
            LocalizedCommandException le = (LocalizedCommandException) e;
            if (le.getResponseHandler() == null) {
                le.setResponseHandler(responseHandler);
            }
        }
    }

    @Override
    public String getLocalizedMessage() {
        if (responseHandler != null)
            return responseHandler.getMessage(this);
        return super.getLocalizedMessage();
    }
}
