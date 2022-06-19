package net.octopvp.commander.argument;

import net.octopvp.commander.annotation.Dependency;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.exception.CommandParseException;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.lang.LocalizedCommandException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.util.Primitives;
import net.octopvp.commander.validator.Validator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArgumentParser {
    public static Object[] parseArguments(CommandContext ctx, CommandArgs cArgs) {
        try {
            Object[] arguments = new Object[ctx.getCommandInfo().getParameters().length];
            for (int i = 0; i < ctx.getCommandInfo().getParameters().length; i++) {
                ParameterInfo parameter = ctx.getCommandInfo().getParameters()[i];
                if (parameter.isFlag()) {
                    if (cArgs.getFlags() == null) {
                        throw new CommandParseException("flags.null");
                    }
                    List<String> paramFlags = parameter.getFlags();
                    for (String paramFlag : paramFlags) {
                        String f = cArgs.getFlags().get(paramFlag);
                        if (f == null) {
                            arguments[i] = Primitives.getDefaultValue(parameter.getParameter().getType());
                            break;
                        }
                        ArrayDeque<String> deque = new ArrayDeque<>();
                        deque.add(f);
                        Object value = parameter.getProvider().provide(ctx, ctx.getCommandInfo(), parameter, deque);
                        if (value != null) {
                            validate(value, parameter, ctx);
                            arguments[i] = value;
                            break;
                        }
                    }
                    continue;
                }
                if (parameter.isSwitch()) {
                    if (cArgs.getSwitches() == null) {
                        throw new CommandParseException("switches.null");
                    }
                    List<String> switches = parameter.getSwitches();
                    Boolean b = cArgs.getSwitches().entrySet().stream().filter(e -> switches.contains(e.getKey())).map(Map.Entry::getValue).findFirst().orElse(null);
                    //Boolean b = cArgs.getSwitches().get(parameter.getSwitches());
                    if (b != null) validate(b, parameter, ctx);
                    arguments[i] = b != null && b;
                    continue;
                }
                if (parameter.getParameter().isAnnotationPresent(Dependency.class)) {
                    Class<?> classType = parameter.getParameter().getType();
                    Supplier<?> supplier = cArgs.getCommander().getDependencies().get(classType);
                    if (supplier == null) {
                        throw new CommandParseException("dependency.not-found", classType.getName()); //maybe let the user control this?
                    }
                    arguments[i] = supplier.get();
                    continue;
                }
                Provider<?> provider = parameter.getProvider();

                if (provider == null) {
                    throw new CommandParseException("provider.not-found", parameter.getParameter().getType().getName());
                }
                Object obj;
                try {
                    obj = provider.provide(ctx, ctx.getCommandInfo(), parameter, cArgs.getArgs());
                } catch (Exception e) {
                    //e.printStackTrace();
                    if (e instanceof InvalidArgsException) {
                        throw new CommandParseException(e);
                    }
                    if (provider.provideUsageOnException()) {
                        throw new InvalidArgsException(ctx.getCommandInfo());
                    }
                    if (provider.failOnExceptionIgnoreOptional()) {
                        throw new CommandParseException(parameter.getName(), e);
                    }
                    if (parameter.isOptional()) {
                        obj = null;
                    } else if (provider.failOnException())
                        throw new CommandParseException(parameter.getName(), e);
                    else {
                        obj = null;
                    }
                }
                if (obj == null) {
                    obj = provider.provideDefault(ctx, ctx.getCommandInfo(), parameter, cArgs.getArgs());
                }

                if (obj == null && parameter.isRequired()) {
                    throw new InvalidArgsException(ctx.getCommandInfo());
                    //throw new CommandParseException("Required argument " + parameter.getName() + " is null!");
                }
                if (obj != null) validate(obj, parameter, ctx);
                arguments[i] = obj;
            }
            return arguments;
        } catch (CommandException e) {
            LocalizedCommandException.checkResponseHandlerNull(e, ctx.getCommandInfo().getCommander().getResponseHandler());
            ctx.getCommandInfo().getCommander().getPlatform().handleCommandException(ctx, e);
        }
        return null;
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
            }
        }
        if (currentArgIsQuoted) {
            throw new CommandParseException("quote.unclosed");
        }
        if (sb.length() > 0) {
            out.add(sb.toString().trim());
        }
        return out;
    }
}
