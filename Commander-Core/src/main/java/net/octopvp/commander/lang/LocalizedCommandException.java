package net.octopvp.commander.lang;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.exception.CommandException;

@Getter
@Setter
public class LocalizedCommandException extends CommandException {
    private final String key;
    private ResponseHandler responseHandler;

    private Object[] placeholders;

    public LocalizedCommandException(String key, Object... placeholders) {
        this.key = key;
        this.placeholders = placeholders;
    }

    /*
    public LocalizedCommandException(String key, Exception cause, Object... placeholders) {
        super(cause);
        this.key = key;
        this.placeholders = placeholders;
        //add cause
    }
     */

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
        if (responseHandler != null) {
            return responseHandler.getMessage(this, placeholders);
        }
        return super.getLocalizedMessage();
    }
}
