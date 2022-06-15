package net.octopvp.commander.lang;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

@Getter
public class DefaultResponseHandler implements ResponseHandler {
    private final Locale locale;

    private final ResourceBundle bundle;

    public DefaultResponseHandler(Locale locale) {
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle("commander", locale);
    }

    @Override
    public String getMessage(Exception e) {
        if (e instanceof LocalizedCommandException) {
            LocalizedCommandException le = (LocalizedCommandException) e;
            return getMessage(le.getKey());
        }
        return e.getMessage();
    }

    @Override
    public String getMessage(String key, Object... placeholders) {
        return String.format(bundle.getString(key), placeholders);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
