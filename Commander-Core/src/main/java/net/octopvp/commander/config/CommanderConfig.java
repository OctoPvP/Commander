package net.octopvp.commander.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.commander.lang.ResponseHandler;

import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
public class CommanderConfig {
    private boolean defaultRequired = true, joinArgsWithQuotes = true, checkPermissionsOnSuggestion = true, showNextSuggestionOnlyIfEndsWithSpace = true,
            filterSuggestions = true;

    private String optionalPrefix = "[", optionalSuffix = "]";
    private String requiredPrefix = "<", requiredSuffix = ">";

    private String flagPrefix = "-", switchPrefix = "-";

    private String commandPrefix = "/";

    private ResponseHandler responseHandler;

    private Locale locale;

    public static class Builder {
        private final CommanderConfig config = new CommanderConfig();

        public Builder setDefaultRequired(boolean defaultRequired) {
            config.setDefaultRequired(defaultRequired);
            return this;
        }

        public Builder setOptionalPrefix(String optionalPrefix) {
            config.setOptionalPrefix(optionalPrefix);
            return this;
        }

        public Builder setOptionalSuffix(String optionalSuffix) {
            config.setOptionalSuffix(optionalSuffix);
            return this;
        }

        public Builder setRequiredPrefix(String requiredPrefix) {
            config.setRequiredPrefix(requiredPrefix);
            return this;
        }

        public Builder setRequiredSuffix(String requiredSuffix) {
            config.setRequiredSuffix(requiredSuffix);
            return this;
        }

        public Builder setFlagPrefix(String flagPrefix) {
            config.setFlagPrefix(flagPrefix);
            return this;
        }

        public Builder setSwitchPrefix(String switchPrefix) {
            config.setSwitchPrefix(switchPrefix);
            return this;
        }

        public Builder setCommandPrefix(String commandPrefix) {
            config.setCommandPrefix(commandPrefix);
            return this;
        }

        public Builder setJoinArgsWithQuotes(boolean joinArgsWithQuotes) {
            config.setJoinArgsWithQuotes(joinArgsWithQuotes);
            return this;
        }

        public Builder setCheckPermissionsOnSuggestion(boolean checkPermissionsOnSuggestion) {
            config.setCheckPermissionsOnSuggestion(checkPermissionsOnSuggestion);
            return this;
        }

        public Builder setShowNextSuggestionOnlyIfEndsWithSpace(boolean showNextSuggestionOnlyIfEndsWithSpace) {
            config.setShowNextSuggestionOnlyIfEndsWithSpace(showNextSuggestionOnlyIfEndsWithSpace);
            return this;
        }

        public Builder setFilterSuggestions(boolean filterSuggestions) {
            config.setFilterSuggestions(filterSuggestions);
            return this;
        }

        public Builder setResponseHandler(ResponseHandler responseHandler) {
            config.setResponseHandler(responseHandler);
            return this;
        }

        public Builder setLocale(Locale locale) {
            config.setLocale(locale);
            return this;
        }

        public CommanderConfig build() {
            return config;
        }
    }
}
