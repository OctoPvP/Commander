package net.octopvp.commander.command;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.Commander;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import net.octopvp.commander.annotation.Dependency;
import net.octopvp.commander.annotation.Permission;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class CommandInfo { //This is the object that is stored in the command map, there should only be one instance of this object per command
    private ParameterInfo[] parameters;

    private ParameterInfo[] parametersExcludingSender;
    private String name;
    private String description;
    private String usage;
    private String[] aliases;
    private Method method;
    private Object instance; // Nullable; if null, the method is static

    private Map<Class<? extends Annotation>, Annotation> annotations;

    private Commander commander;

    private String permission;

    private double cooldown;
    private TimeUnit cooldownUnit;

    private Map<UUID, Long> cooldownMap;

    private boolean subCommand = false, parentCommand = false;
    private CommandInfo parent;
    private List<CommandInfo> subCommands;
    private boolean hasFlags, foundFlagsAlready;
    private boolean hasSwitches, foundSwitchesAlready;

    public CommandInfo(ParameterInfo[] parameters, String name, String description, String usage, String[] aliases, Method method, Object instance, Map<Class<? extends Annotation>, Annotation> annotations, Commander commander) {
        this.parameters = parameters;
        this.name = name.toLowerCase();
        this.description = description;
        this.usage = usage;
        this.method = method;
        this.instance = instance;
        this.annotations = annotations;
        this.commander = commander;
        if (isAnnotationPresent(Permission.class)) {
            this.permission = getAnnotation(Permission.class).value();
        } else {
            this.permission = null;
        }
        if (isAnnotationPresent(Cooldown.class)) {
            this.cooldown = getAnnotation(Cooldown.class).value();
            this.cooldownUnit = getAnnotation(Cooldown.class).unit();
            this.cooldownMap = new HashMap<>();
        }
        //make sure aliases are lowercase
        this.aliases = aliases;
        for (int i = 0; i < aliases.length; i++) {
            this.aliases[i] = aliases[i].toLowerCase();
        }
    }

    public Command getAnnotation() {
        return method.getAnnotation(Command.class);
    }

    public String getUsage() {
        if (usage == null || usage.equals("<<generate>>")) {
            String optionalPrefix = commander.getConfig().getOptionalPrefix(), requiredPrefix = commander.getConfig().getRequiredPrefix(),
                    optionalSuffix = commander.getConfig().getOptionalSuffix(), requiredSuffix = commander.getConfig().getRequiredSuffix();
            if (parentCommand) {
                StringBuilder builder = new StringBuilder();
                builder.append(commander.getConfig().getRequiredPrefix());
                for (CommandInfo command : subCommands) {
                    builder.append(command.getName()).append("/");
                }
                builder.deleteCharAt(builder.length() - 1)
                        .append(commander.getConfig().getRequiredSuffix());
                return builder.toString().trim();
            } else {
                StringBuilder builder = new StringBuilder();
                for (ParameterInfo parameter : parameters) {
                    if (parameter.hideFromUsage()) {
                        continue;
                    }
                    if (parameter.isSwitch()) {
                        builder.append(optionalPrefix)
                                .append("-").append(parameter.getSwitchUsageName())
                                .append(optionalSuffix)
                                .append(" ");
                    } else if (parameter.isFlag()) {
                        builder.append(optionalPrefix)
                                .append("-").append(parameter.getFlagUsageName())
                                .append(optionalSuffix)
                                .append(" ");
                    } else {
                        boolean optional = parameter.isOptional();
                        builder.append(optional ? optionalPrefix : requiredPrefix)
                                .append(parameter.getName())
                                .append(optional ? optionalSuffix : requiredSuffix)
                                .append(" ");
                    }
                }
                this.usage = builder.toString().trim();
            }
        }
        return usage;
    }

    public String getFullUsage() {
        if (isSubCommand()) {
            return commander.getConfig().getCommandPrefix() + getParent().getName() + " " + getName() + " " + getUsage();
        }
        return commander.getConfig().getCommandPrefix() + getName() + " " + getUsage();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return annotations.containsKey(annotation) || (method != null && method.isAnnotationPresent(annotation));
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return (T) annotations.get(annotation);
    }

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

    public boolean cooldownEnabled() {
        return cooldown > 0;
    }

    public boolean isOnCooldown(UUID uuid) {
        if (cooldownMap == null) {
            return false;
        }
        //check if they are on a cooldown, if so, check if it has expired, if it has expired, remove
        Long time = cooldownMap.get(uuid);
        if (time == null) return false;
        if (time - System.currentTimeMillis() <= 0) { // Cooldown expired
            cooldownMap.remove(uuid);
            return false;
        }
        return true;
    }

    public double getCooldownMillis(UUID uuid) {
        if (cooldownMap == null) {
            return 0;
        }
        return cooldownMap.get(uuid) - System.currentTimeMillis();
    }

    public double getCooldownSeconds(UUID uuid) {
        return getCooldownMillis(uuid) / 1000d;
    }

    public void addCooldown(UUID uuid) {
        if (cooldownMap == null) {
            return;
        }
        cooldownMap.put(uuid, System.currentTimeMillis() + cooldownUnit.toMillis((long) cooldown));
    }

    public boolean isNormalCommand() {
        return !isSubCommand() && !isParentCommand();
    }

    public CommandInfo getSubCommand(String name) {
        return subCommands.stream().filter(command -> command.getName().equalsIgnoreCase(name) || Arrays.asList(command.aliases).contains(name)).findFirst().orElse(null);
    }

    public ParameterInfo[] getCommandParameters() {
        if (parametersExcludingSender != null) return parametersExcludingSender;
        List<ParameterInfo> list = new ArrayList<>();
        for (ParameterInfo parameter : parameters) {
            if (!commander.getPlatform().isSenderParameter(parameter) && !parameter.getParameter().isAnnotationPresent(Dependency.class)) {
                list.add(parameter);
            }
        }
        return parametersExcludingSender = list.toArray(new ParameterInfo[list.size()]);
    }

    @Override
    public String toString() {
        return "CommandInfo{" +
                "parameters=" + Arrays.toString(parameters) +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", usage='" + usage + '\'' +
                ", aliases=" + aliases.length +
                ", method=" + method +
                ", instance=" + instance +
                ", annotations=" + annotations +
                ", commander=" + commander +
                ", permission='" + permission + '\'' +
                ", cooldown=" + cooldown +
                ", cooldownUnit=" + cooldownUnit +
                ", cooldownMap=" + cooldownMap +
                ", subCommand=" + subCommand +
                ", parentCommand=" + parentCommand +
                ", parent=" + parent +
                ", subCommands=" + subCommands.size() +
                ", hasFlags=" + hasFlags +
                ", foundFlagsAlready=" + foundFlagsAlready +
                ", hasSwitches=" + hasSwitches +
                ", foundSwitchesAlready=" + foundSwitchesAlready +
                '}';
    }
}
