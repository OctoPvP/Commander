package net.octopvp.commander.argument;

import net.octopvp.commander.annotation.Dependency;
import net.octopvp.commander.annotation.Flag;
import net.octopvp.commander.annotation.Switch;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandParseException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.util.Primitives;
import net.octopvp.commander.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArgumentParser {
    private static Object instance;
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
                validator.validate(obj,parameter,ctx);
            }
        }
    }
    /*
    TODO this
    public static List<String> combineMultiWordArguments() {
        String[] args = {"test", "\"test1", "test2", "test3\"", "test4"};
        //convert this to ["test", "test1 test2 test3", "test4"]
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            if (s.startsWith("\"") && s.endsWith("\"")) {
                sb.append(s.substring(1, s.length() - 1));
            }
            else if (s.startsWith("\"")) {
                sb.append(s.substring(1));
            }
            else if (s.endsWith("\"")) {
                sb.append(s.substring(0, s.length() - 1));
                list.add(sb.toString());
                sb = new StringBuilder();
            }
            else {
                sb.append(s);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list;
    }
     */
}
