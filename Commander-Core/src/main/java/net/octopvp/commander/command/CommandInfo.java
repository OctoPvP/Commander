package net.octopvp.commander.command;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.Commander;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

@Getter
@Setter
public class CommandInfo {
    private ParameterInfo[] parameters;
    private String name;
    private String description;
    private String usage;
    private String[] aliases;
    private Method method;
    private Object instance; // Nullable; if null, the method is static

    private Map<Class<? extends Annotation>,Annotation> annotations;

    private Commander commander;

    private String permission;

    private boolean async;

    public CommandInfo(ParameterInfo[] parameters, String name, String description, String usage, String[] aliases, Method method, Object instance, Map<Class<? extends Annotation>, Annotation> annotations, Commander commander) {
        this.parameters = parameters;
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.method = method;
        this.instance = instance;
        this.annotations = annotations;
        this.commander = commander;
        if (isAnnotationPresent(Permission.class)){
            this.permission = getAnnotation(Permission.class).value();
        }

    }

    public Command getAnnotation() {
        return method.getAnnotation(Command.class);
    }

    public String getUsage() {
        if (usage == null || usage.equalsIgnoreCase("<<generate>>")) {
            StringBuilder builder = new StringBuilder();
            for (ParameterInfo parameter : parameters) {
                boolean optional = parameter.isOptional();
                builder.append(optional ? commander.getConfig().getOptionalPrefix() : commander.getConfig().getRequiredPrefix())
                        .append(parameter.getName())
                        .append(optional ? commander.getConfig().getOptionalSuffix() : commander.getConfig().getRequiredSuffix())
                        .append(" ");
            }
            this.usage = builder.toString().trim();
        }
        return usage;
    }
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return annotations.containsKey(annotation);
    }
    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return (T) annotations.get(annotation);
    }

    private boolean hasFlags, foundFlagsAlready;
    private boolean hasSwitches, foundSwitchesAlready;

    public boolean hasFlags() {
        if (foundFlagsAlready) {
            return hasFlags;
        }
        boolean b = false;
        for (ParameterInfo parameter : parameters) {
            if (parameter.isFlag()) {
                b = true;
            }
        }
        return hasFlags = b;
    }
    public boolean hasSwitches() {
        if (foundSwitchesAlready) {
            return hasSwitches;
        }
        boolean b = false;
        for (ParameterInfo parameter : parameters) {
            if (parameter.isSwitch()) {
                b = true;
            }
        }
        return hasSwitches = b;
    }

}
