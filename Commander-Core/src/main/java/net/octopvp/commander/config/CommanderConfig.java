/*
 * Copyright (c) Badbird5907 2022.
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.octopvp.commander.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.commander.lang.ResponseHandler;

import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
public class CommanderConfig {
    private boolean defaultRequired = true, joinArgsWithQuotes = false, checkPermissionsOnSuggestion = true, showNextSuggestionOnlyIfEndsWithSpace = true,
            filterSuggestions = true, defaultAsync = false;

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

        public Builder setDefaultAsync(boolean defaultAsync) {
            config.setDefaultAsync(defaultAsync);
            return this;
        }

        public CommanderConfig build() {
            return config;
        }
    }
}
