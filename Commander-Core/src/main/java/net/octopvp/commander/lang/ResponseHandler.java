package net.octopvp.commander.lang;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public interface ResponseHandler {
    String getMessage(Exception e, Object... placeholders);

    String getMessage(String key, Object... placeholders);

    Locale getLocale();

    void addBundle(ResourceBundle bundle);

    List<ResourceBundle> getBundles();

    void overrideKey(String key, String value);

    Map<String, String> getOverrides();
}
