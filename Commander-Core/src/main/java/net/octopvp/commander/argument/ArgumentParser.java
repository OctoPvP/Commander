package net.octopvp.commander.argument;

import net.octopvp.commander.annotation.Dependency;
import net.octopvp.commander.annotation.Flag;
import net.octopvp.commander.annotation.Switch;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandParseException;
import net.octopvp.commander.provider.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ArgumentParser {
    public static Object[] parseArguments(CommandContext ctx, CommandArgs cArgs) {
        Object[] arguments = new Object[ctx.getCommandInfo().getParameters().length];
        for (int i = 0; i < ctx.getCommandInfo().getParameters().length; i++) {
            ParameterInfo parameter = ctx.getCommandInfo().getParameters()[i];
            if (parameter.isFlag()) {
                Flag flag = parameter.getParameter().getAnnotation(Flag.class);
                String f = cArgs.getFlags().get(flag.value());
                if (f == null || f.isEmpty())
                    f = parameter.getParameter().getName();
                arguments[i] = f;
                continue;
            }
            if (parameter.isSwitch()) {
                Switch paramSwitch = parameter.getParameter().getAnnotation(Switch.class);
                boolean b;
                if (paramSwitch.value().isEmpty()) {
                    b = cArgs.getSwitches().get(parameter.getParameter().getName());
                } else {
                    b = cArgs.getSwitches().get(paramSwitch.value());
                }
                arguments[i] = b;
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
                if (provider.failOnException())
                    throw new CommandParseException("Failed to parse argument " + parameter.getParameter().getName(), e);
                else {
                    obj = provider.provideDefault(ctx, ctx.getCommandInfo(), parameter, cArgs.getArgs());
                }
            }
            if (obj == null) {
                obj = provider.provideDefault(ctx, ctx.getCommandInfo(), parameter, cArgs.getArgs());
            }
        }
        return arguments;
    }

    /**
     * Combines multi word arguments into a single string
     *
     * @param args
     * @return
     * @author Jonah Seguin
     */
    public static List<String> combineMultiWordArguments(List<String> args) {
        List<String> argList = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (!arg.isEmpty()) {
                final char c = arg.charAt(0);
                if (c == '"' || c == '\'') {
                    final StringBuilder builder = new StringBuilder();
                    int endIndex;
                    for (endIndex = i; endIndex < args.size(); endIndex++) {
                        final String arg2 = args.get(endIndex);
                        if (arg2.charAt(arg2.length() - 1) == c && arg2.length() > 1) {
                            if (endIndex != i) {
                                builder.append(' ');
                            }
                            builder.append(arg2, endIndex == i ? 1 : 0, arg2.length() - 1);
                            break;
                        } else if (endIndex == i) {
                            builder.append(arg2.substring(1));
                        } else {
                            builder.append(' ').append(arg2);
                        }
                    }
                    if (endIndex < args.size()) {
                        arg = builder.toString();
                        i = endIndex;
                    }
                }
            }
            if (!arg.isEmpty()) {
                argList.add(arg);
            }
        }
        return argList;
    }

}
