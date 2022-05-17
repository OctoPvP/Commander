package net.octopvp.commander.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommanderConfig {
    private boolean defaultRequired = true;

    private String optionalPrefix = "[", optionalSuffix = "]";
    private String requiredPrefix = "<", requiredSuffix = ">";

    private String flagPrefix = "-", switchPrefix = "-";

    private String commandPrefix = "/";

    public static class Builder {
        private CommanderConfig config = new CommanderConfig();

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

        public CommanderConfig build() {
            return config;
        }
    }
}
