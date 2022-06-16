package net.octopvp.commander.lang;

import lombok.Getter;

import java.util.*;

@Getter
public class DefaultResponseHandler implements ResponseHandler {
    private final Locale locale;

    private final List<ResourceBundle> bundles = new ArrayList<>();

    private final Map<String, String> overrides = new HashMap<>();

    public DefaultResponseHandler(Locale locale) {
        this.locale = locale;
        bundles.add(ResourceBundle.getBundle("commander", locale));
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
        String msg = null;
        if (overrides.containsKey(key)) {
            msg = overrides.get(key);
        } else {
            //make sure that the bundles added last are checked first, so that the overrides take precedence
            for (int i = bundles.size() - 1; i >= 0; i--) {
                ResourceBundle bundle = bundles.get(i);
                if (bundle.containsKey(key)) {
                    msg = bundle.getString(key);
                    break;
                }
            }
        }
        if (msg == null) {
            msg = key;
        }
        return String.format(msg, placeholders);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void addBundle(ResourceBundle bundle) {
        bundles.add(bundle);
    }

    @Override
    public void overrideKey(String key, String value) {
        overrides.put(key, value);
    }
}
