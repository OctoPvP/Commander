package net.octopvp.commander.lang;

import java.util.Locale;

public interface ResponseHandler {
    String getMessage(Exception e);

    String getMessage(String key, Object... placeholders);

    Locale getLocale();
}
