package net.octopvp.commander.argument;

import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Dependency;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandParseException;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.util.Primitives;
import net.octopvp.commander.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArgumentParser {
    public static Object[] parseArguments(CommandContext ctx, CommandArgs cArgs) {
        Object[] arguments = new Object[ctx.getCommandInfo().getParameters().length];
        for (int i = 0; i < ctx.getCommandInfo().getParameters().length; i++) {
            ParameterInfo parameter = ctx.getCommandInfo().getParameters()[i];
            if (parameter.isFlag()) {
                if (cArgs.getFlags() == null) {
                    throw new CommandParseException("Flags are null!");
                }
                String f = cArgs.getFlags().get(parameter.getFlag());
                if (f != null) validate(f, parameter, ctx);
                arguments[i] = f;
                continue;
            }
            if (parameter.isSwitch()) {
                if (cArgs.getSwitches() == null) {
                    throw new CommandParseException("Switches are null!");
                }
                Boolean b = cArgs.getSwitches().get(parameter.getSwitch());
                if (b != null) validate(b, parameter, ctx);
                arguments[i] = b != null && b;
                continue;
            }
            if (parameter.getParameter().isAnnotationPresent(Dependency.class)) {
                Class<?> classType = parameter.getParameter().getType();
                Supplier<?> supplier = cArgs.getCommander().getDependencies().get(classType);
                if (supplier == null) {
                    throw new CommandParseException("Dependency not found for " + classType.getName()); //maybe let the user control this?
                }
                arguments[i] = supplier.get();
                continue;
            }
            Provider<?> provider = parameter.getProvider();

            if (provider == null) {
                throw new CommandParseException("No provider found for " + parameter.getParameter().getType().getName());
            }
            Object obj;
            try {
                obj = provider.provide(ctx, ctx.getCommandInfo(), parameter, cArgs.getArgs());
            } catch (Exception e) {
                if (e instanceof InvalidArgsException) {
                    //cArgs.getCommander().getPlatform().getHelpService().sendHelp(ctx, ctx.getCommandSender());
                    //return null;
                    throw e;
                }
                if (provider.provideUsageOnException()) {
                    throw new InvalidArgsException(ctx.getCommandInfo());
                }
                if (provider.failOnExceptionIgnoreOptional()) {
                    throw new CommandParseException("Failed to parse argument " + parameter.getParameter().getName(), e);
                }
                if (parameter.isOptional()) {
                    obj = null;
                } else if (provider.failOnException())
                    throw new CommandParseException("Failed to parse argument " + parameter.getParameter().getName(), e);
                else {
                    obj = null;
                }
            }
            if (obj == null) {
                obj = provider.provideDefault(ctx, ctx.getCommandInfo(), parameter, cArgs.getArgs());
            }

            if (obj == null && parameter.isRequired()) {
                throw new CommandParseException("Required argument " + parameter.getParameter().getName() + " is null!");
            }
            if (obj != null) validate(obj, parameter, ctx);
            arguments[i] = obj;
        }
        return arguments;
    }

    private static void validate(Object obj, ParameterInfo parameter, CommandContext ctx) {
        Class<?> type = Primitives.wrap(parameter.getParameter().getType());
        for (Map.Entry<Class<?>, Validator<Object>> entry : ctx.getCommandInfo().getCommander().getValidators().entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                Validator<Object> validator = entry.getValue();
                validator.validate(obj, parameter, ctx);
            }
        }
    }

    public static List<String> combineArgs(List<String> in) {
        //combine arguments that start with " or ' with the next argument until the next " or '
        // [arg1,"arg2,arg3,arg4",arg5] -> [arg1, arg2 arg3 arg4, arg5]
        List<String> out = new ArrayList<>();

        boolean currentArgIsQuoted = false;
        StringBuilder sb = new StringBuilder();
        for (String s : in) {
            String arg = s.trim();
            if ((!arg.startsWith("\"") && !arg.startsWith("'")) && (!arg.endsWith("\"") && !arg.endsWith("'"))) {
                if (currentArgIsQuoted) {
                    sb.append(" ").append(arg);
                    continue;
                } else {
                    out.add(arg);
                    continue;
                }
            }
            if (arg.startsWith("\"") || arg.startsWith("'")) {
                sb.append(arg.substring(1));
                currentArgIsQuoted = true;
                continue;
            }
            if (arg.endsWith("\"") || arg.endsWith("'")) {
                sb.append(" ").append(arg, 0, arg.length() - 1);
                currentArgIsQuoted = false;
                out.add(sb.toString().trim());
                sb = new StringBuilder();
                continue;
            }
        }
        System.out.println("Out: " + out + " | " + sb);
        if (currentArgIsQuoted) {
            throw new CommandParseException("Unclosed quote!");
        }
        if (sb.length() > 0) {
            out.add(sb.toString().trim());
        }
        return out;
    }
}
