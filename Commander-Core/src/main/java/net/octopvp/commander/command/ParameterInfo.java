package net.octopvp.commander.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.commander.Commander;
import net.octopvp.commander.annotation.*;
import net.octopvp.commander.provider.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class ParameterInfo {
    private final Parameter parameter;

    private final Commander commander;

    private Provider<?> provider;

    private boolean alreadyFoundProvider = false;

    public Provider<?> getProvider() {
        if (provider == null && !alreadyFoundProvider) {
            provider = commander.getArgumentProviders().stream().filter(p -> p.getType().equals(parameter.getType()) || Arrays.asList(p.getExtraTypes()).contains(parameter.getType())).findFirst().orElse(null);
            alreadyFoundProvider = true;
        }
        return provider;
    }

    public boolean isOptional() {
        if (parameter.isAnnotationPresent(Optional.class)) {
            return true;
        }
        return !commander.getConfig().isDefaultRequired();
    }

    public boolean isRequired() {
        if (parameter.isAnnotationPresent(Required.class)) {
            return true;
        }
        return commander.getConfig().isDefaultRequired();
    }

    public String getName() {
        if (parameter.isAnnotationPresent(Name.class))
            return parameter.getAnnotation(Name.class).value();
        return parameter.getName();
    }

    public boolean isFlag() {
        return parameter.isAnnotationPresent(Flag.class);
    }
    public boolean isSwitch() {
        return parameter.isAnnotationPresent(Switch.class);
    }
}
