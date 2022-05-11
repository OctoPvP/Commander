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
            provider = commander.getArgumentProviders().stream().filter(p -> {
                boolean matchWithInstanceOf = p.matchWithInstanceOf();
                if (matchWithInstanceOf) {
                    return parameter.getType().isAssignableFrom(p.getType()) || (p.getExtraTypes() != null && Arrays.asList(p.getExtraTypes()).stream().anyMatch(t -> parameter.getType().isAssignableFrom(t)));
                }
                return p.getType().equals(parameter.getType()) || (p.getExtraTypes() != null && Arrays.asList(p.getExtraTypes()).contains(parameter.getType()));
            }).findFirst().orElse(null);
            alreadyFoundProvider = true;
        }
        return provider;
    }

    public boolean isOptional() {
        if (parameter.isAnnotationPresent(Optional.class)) {
            return true;
        }
        if (parameter.isAnnotationPresent(Required.class)) {
            return false;
        }
        return !commander.getConfig().isDefaultRequired();
    }

    public boolean isRequired() {
        if (parameter.isAnnotationPresent(Required.class)) {
            return true;
        }
        if (parameter.isAnnotationPresent(Optional.class)) {
            return false;
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
    public String getFlag() {
        if (parameter.isAnnotationPresent(Flag.class)) {
            Flag f = parameter.getAnnotation(Flag.class);
            return f.value() == null || f.value().isEmpty() ? parameter.getName() : f.value();
        }
        return null;
    }

    public boolean isSwitch() {
        return parameter.isAnnotationPresent(Switch.class);
    }
    public String getSwitch() {
        if (parameter.isAnnotationPresent(Switch.class)) {
            Switch s = parameter.getAnnotation(Switch.class);
            return s.value() == null || s.value().isEmpty() ? parameter.getName() : s.value();
        }
        return null;
    }
}
