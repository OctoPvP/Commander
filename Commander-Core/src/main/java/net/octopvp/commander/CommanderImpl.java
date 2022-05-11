package net.octopvp.commander;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.DistributeOnMethods;
import net.octopvp.commander.argument.ArgumentParser;
import net.octopvp.commander.argument.CommandArgs;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.config.CommanderConfig;
import net.octopvp.commander.exception.CommandNotFoundException;
import net.octopvp.commander.exception.CommandParseException;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.provider.impl.IntegerProvider;
import net.octopvp.commander.provider.impl.SenderProvider;
import net.octopvp.commander.provider.impl.StringProvider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CommanderImpl implements Commander {
    private CommanderPlatform platform;
    private CommanderConfig config;

    private Set<Provider<?>> argumentProviders = new HashSet<>();

    private Map<Class<?>, Supplier<?>> dependencies = new HashMap<>();

    private Map<String, CommandInfo> commandMap = new HashMap<>();

    private List<Consumer<CommandContext>> preProcessors = new ArrayList<>();

    public CommanderImpl(CommanderPlatform platform) {
        this.platform = platform;
        this.config = new CommanderConfig();
    }

    public CommanderImpl(CommanderPlatform platform, CommanderConfig config) {
        this.platform = platform;
        this.config = config;
    }

    @Override
    public Commander init() {
        registerProvider(new IntegerProvider());
        registerProvider(new SenderProvider());
        registerProvider(new StringProvider());
        return this;
    }

    @Override
    public Commander register(Object... objects) {
        for (Object object : objects) {
            List<Annotation> distributedAnnotations = new ArrayList<>();
            for (Annotation annotation : object.getClass().getDeclaredAnnotations()) {
                if (annotation.getClass().isAnnotationPresent(DistributeOnMethods.class)) {
                    distributedAnnotations.add(annotation);
                }
            }
            for (Method method : object.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Command.class)) {
                    continue;
                }
                Command command = method.getAnnotation(Command.class);
                String name = command.name();
                List<ParameterInfo> parameters = new ArrayList<>();
                for (Parameter parameter : method.getParameters()) {
                    parameters.add(new ParameterInfo(parameter, this));
                }
                Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
                for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                    annotations.put(declaredAnnotation.annotationType(), declaredAnnotation);
                }
                for (Annotation distributedAnnotation : distributedAnnotations) {
                    annotations.put(distributedAnnotation.annotationType(), distributedAnnotation);
                }
                CommandInfo commandInfo = new CommandInfo(parameters.toArray(new ParameterInfo[0]), name, command.description(), command.usage(), command.aliases(), method, object, annotations, this);
                commandMap.put(name, commandInfo);
                for (String alias : command.aliases()) {
                    commandMap.put(alias, commandInfo);
                }
            }
        }
        return this;
    }

    @Override
    public CommanderConfig getConfig() {
        return config;
    }

    @Override
    public Commander setConfig(CommanderConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public Collection<Provider<?>> getArgumentProviders() {
        return this.argumentProviders;
    }

    @Override
    public Commander registerProvider(Provider<?> provider) {
        this.argumentProviders.add(provider);
        return this;
    }

    @Override
    public CommanderPlatform getPlatform() {
        return platform;
    }

    @Override
    public <T> Commander registerDependency(Class<T> type, Supplier<T> supplier) {
        this.dependencies.put(type, supplier);
        return this;
    }

    @Override
    public <T> Commander registerDependency(Class<T> type, T val) {
        this.dependencies.put(type, () -> val);
        return this;
    }

    @Override
    public Map<Class<?>, Supplier<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public Commander registerCommandPreProcessor(Consumer<CommandContext> preProcessor) {
        preProcessors.add(preProcessor);
        return this;
    }

    @Override
    public void executeCommand(CoreCommandSender sender, String label, String[] args) throws CommandParseException {
        CommandInfo commandInfo = commandMap.get(label);
        if (commandInfo == null) {
            throw new CommandNotFoundException("Could not find command handler for " + label);
        }

        String[] argsCopy = new String[args.length];
        System.arraycopy(args, 0, argsCopy, 0, args.length);
        //List<String> argsList = ArgumentParser.combineMultiWordArguments(Arrays.asList(args));
        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        CommandArgs cArgs = new CommandArgs(this, args, commandInfo.hasSwitches() ? extractSwitches(argsList, commandInfo.getParameters()) : null, commandInfo.hasFlags() ? extractFlags(argsList, commandInfo.getParameters()) : null,argsList);

        CommandContext context = new CommandContext(commandInfo, label.toLowerCase(), argsCopy, sender, cArgs);
        for (Consumer<CommandContext> preProcessor : preProcessors) {
            preProcessor.accept(context);
        }
        try {
            if (commandInfo.getPermission() != null && !sender.hasPermission(commandInfo.getPermission())) {
                throw new CommandParseException("You do not have permission to use this command.");
            }

            Object[] arguments = ArgumentParser.parseArguments(context,cArgs);

            try {
                context.getCommandInfo().getMethod().invoke(context.getCommandInfo().getInstance(), arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (CommandNotFoundException | CommandParseException e) {
            platform.handleCommandException(context, e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> extractFlags(final List<String> args, final ParameterInfo[] params) {
        List<ParameterInfo> paramsList = Arrays.asList(params);
        Map<String, String> flags = new HashMap<>();
        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            if (arg.startsWith(config.getFlagPrefix())) {
                String flag = arg.substring(config.getFlagPrefix().length());
                if (flags.containsKey(flag)) {
                    throw new CommandParseException("Flag " + flag + " is defined multiple times.");
                }
                if (paramsList.stream().noneMatch(p -> p.isFlag() && p.getFlag().equals(flag)))
                    continue;
                iterator.remove();
                if (iterator.hasNext()) {
                    String value = iterator.next();
                    iterator.remove();
                    flags.put(flag, value);
                }else {
                    throw new CommandParseException("Flag " + flag + " requires a value.");
                }
            }
        }
        return flags;
    }
    private Map<String, Boolean> extractSwitches(final List<String> args, final ParameterInfo[] params) {
        List<ParameterInfo> paramsList = Arrays.asList(params);
        Map<String, Boolean> switches = new HashMap<>();
        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            if (arg.startsWith(config.getSwitchPrefix())) {
                String flag = arg.substring(config.getSwitchPrefix().length());
                if (switches.containsKey(flag)) {
                    throw new CommandParseException("Switch " + flag + " is defined multiple times.");
                }
                if (paramsList.stream().noneMatch(p -> p.isSwitch() && p.getSwitch().equals(flag)))
                    continue;
                iterator.remove();
                switches.put(flag, true);

            }
        }
        return switches;
    }
}
