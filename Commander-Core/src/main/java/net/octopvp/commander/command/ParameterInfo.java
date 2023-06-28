/*
 * Copyright (c) Badbird5907 2023.
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

package net.octopvp.commander.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.commander.Commander;
import net.octopvp.commander.annotation.*;
import net.octopvp.commander.provider.Provider;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            /*
            provider = commander.getArgumentProviders().stream().filter(p -> {
                boolean matchWithInstanceOf = p.matchWithInstanceOf();
                if (matchWithInstanceOf) {
                    return parameter.getType().isAssignableFrom(p.getType()) || (p.getExtraTypes() != null && Arrays.asList(p.getExtraTypes()).stream().anyMatch(t -> parameter.getType().isAssignableFrom(t)));
                }
                return p.getType().equals(parameter.getType()) || (p.getExtraTypes() != null && Arrays.asList(p.getExtraTypes()).contains(parameter.getType()));
            }).findFirst().orElse(null);
             */
            java.util.Optional<Map.Entry<Class<?>,Provider<?>>> e = commander.getArgumentProviders().entrySet().stream().filter(entry -> {
                Class<?> clazz = entry.getKey();
                Provider<?> provider = entry.getValue();
                boolean matchWithInstanceOf = provider.matchWithInstanceOf();
                if (matchWithInstanceOf) {
                    return parameter.getType().isAssignableFrom(clazz) || (provider.getExtraTypes() != null && Arrays.asList(provider.getExtraTypes()).stream().anyMatch(t -> parameter.getType().isAssignableFrom(t)));
                }
                return clazz.equals(parameter.getType()) || (provider.getExtraTypes() != null && Arrays.asList(provider.getExtraTypes()).contains(parameter.getType()));
            }).findFirst();
            provider = e.<Provider<?>>map(Map.Entry::getValue).orElse(null);
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

    public List<String> getFlags() {
        if (parameter.isAnnotationPresent(Flag.class)) {
            Flag f = parameter.getAnnotation(Flag.class);
            List<String> flags = new ArrayList<>();
            flags.add(f.value());
            for (String alias : f.aliases()) {
                if (alias != null && !alias.isEmpty()) {
                    flags.add(alias);
                }
            }
            return flags;
            //return f.value() == null || f.value().isEmpty() ? parameter.getName() : f.value();
        }
        return null;
    }

    public boolean isSwitch() {
        return parameter.isAnnotationPresent(Switch.class);
    }

    public List<String> getSwitches() {
        if (parameter.isAnnotationPresent(Switch.class)) {
            Switch s = parameter.getAnnotation(Switch.class);
            List<String> switches = new ArrayList<>();
            for (String alias : s.aliases()) {
                if (alias != null && !alias.isEmpty()) {
                    switches.add(alias);
                }
            }
            if (s.value() != null && !s.value().isEmpty()) {
                switches.add(s.value());
            }
            return switches;
        }
        return null;
    }

    public String getSwitchUsageName() {
        if (parameter.isAnnotationPresent(Switch.class)) {
            Switch s = parameter.getAnnotation(Switch.class);
            return s.value();
        }
        return null;
    }

    public String getFlagUsageName() {
        if (parameter.isAnnotationPresent(Flag.class)) {
            Flag f = parameter.getAnnotation(Flag.class);
            return f.value();
        }
        return null;
    }

    public boolean hideFromUsage() {
        return commander.getPlatform().isSenderParameter(this) || parameter.isAnnotationPresent(Dependency.class) || parameter.isAnnotationPresent(GetArgumentFor.class) || parameter.isAnnotationPresent(Hidden.class);
    }

    @Override
    public String toString() {
        return "ParameterInfo{" +
                "parameter=" + parameter +
                ", commander=" + commander +
                ", provider=" + provider +
                ", alreadyFoundProvider=" + alreadyFoundProvider +
                '}';
    }
}
