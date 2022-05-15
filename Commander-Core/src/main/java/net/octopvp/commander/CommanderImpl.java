package net.octopvp.commander;

import lombok.val;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.DistributeOnMethods;
import net.octopvp.commander.annotation.Range;
import net.octopvp.commander.argument.ArgumentParser;
import net.octopvp.commander.argument.CommandArgs;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.config.CommanderConfig;
import net.octopvp.commander.exception.*;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.provider.impl.IntegerProvider;
import net.octopvp.commander.provider.impl.SenderProvider;
import net.octopvp.commander.provider.impl.StringProvider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.commander.util.Primitives;
import net.octopvp.commander.validator.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CommanderImpl implements Commander {
    private CommanderPlatform platform;
    private CommanderConfig config;

    private Map<Class<?>, Provider<?>> argumentProviders = new HashMap<>();

    private Map<Class<?>, Supplier<?>> dependencies = new HashMap<>();

    private Map<String, CommandInfo> commandMap = new HashMap<>();

    private List<Consumer<CommandContext>> preProcessors = new ArrayList<>();
    private List<BiConsumer<CommandContext, Object>> postProcessors = new ArrayList<>();

    private Map<Class<?>, Validator<Object>> validators = new HashMap<>();

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
        registerProvider(Integer.class, new IntegerProvider());
        registerProvider(CoreCommandSender.class, new SenderProvider());
        registerProvider(String.class, new StringProvider());
        registerCommandPreProcessor(context -> { //Cooldown preprocessor
            if (context.getCommandInfo().cooldownEnabled() && context.getCommandInfo().isOnCooldown(context.getCommandSender().getIdentifier())) {
                throw new CooldownException(context.getCommandInfo().getCooldownSeconds(context.getCommandSender().getIdentifier()));
            }
        });
        registerCommandPostProcessor((context, result) -> {
            if (context.getCommandInfo().cooldownEnabled()) {
                context.getCommandInfo().addCooldown(context.getCommandSender().getIdentifier());
            }
        });
        registerValidator(Number.class, (value, parameter, context) -> {
            Range range = parameter.getParameter().getAnnotation(Range.class);
            if (range != null && (value.doubleValue() > range.max() || value.doubleValue() < range.min())) {
                if (value.doubleValue() == range.defaultValue()) { //All number providers should use Range#defaultValue() if the annotation is present
                    return; //TODO we gotta find a better way to handle primitives, which cant take null
                }
                throw new ValidateException("Value " + value.doubleValue() + " is not in range " + range.min() + " - " + range.max());
            }
        });
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
            boolean classHasMainCommand = object.getClass().isAnnotationPresent(Command.class);
            Command parent = null;
            CommandInfo parentInfo = null;
            if (classHasMainCommand) {
                parent = object.getClass().getAnnotation(Command.class);
                Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
                for (Annotation declaredAnnotation : object.getClass().getDeclaredAnnotations()) {
                    annotations.put(declaredAnnotation.annotationType(), declaredAnnotation);
                }
                for (Annotation distributedAnnotation : distributedAnnotations) {
                    annotations.put(distributedAnnotation.annotationType(), distributedAnnotation);
                }
                parentInfo = new CommandInfo(null, parent.name(), parent.description(), parent.usage(), parent.aliases(), null, object, annotations, this);
                parentInfo.setParentCommand(true);
                parentInfo.setSubCommands(new ArrayList<>());
                commandMap.put(parent.name().toLowerCase(), parentInfo);
                for (String alias : parent.aliases()) {
                    commandMap.put(alias.toLowerCase(), parentInfo);
                }
                platform.registerCommand(parentInfo);
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
                commandInfo.setSubCommand(classHasMainCommand);
                if (classHasMainCommand) {
                    commandInfo.setParent(parentInfo);
                    parentInfo.getSubCommands().add(commandInfo);
                } else {
                    commandMap.put(name.toLowerCase(), commandInfo);
                    for (String alias : command.aliases()) {
                        commandMap.put(alias.toLowerCase(), commandInfo);
                    }
                    platform.registerCommand(commandInfo);
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
    public Map<Class<?>, Provider<?>> getArgumentProviders() {
        return this.argumentProviders;
    }

    @Override
    public Commander registerProvider(Class<?> type, Provider<?> provider) {
        this.argumentProviders.put(Primitives.wrap(type), provider);
        return this;
    }

    @Override
    public Commander removeProvider(Class<?> clazz) {
        this.argumentProviders.entrySet().removeIf(e -> e.getValue().getClass().equals(clazz) || e.getKey().equals(Primitives.wrap(clazz)));
        return this;
    }

    @Override
    public <T> Commander registerValidator(Class<T> clazz, Validator<T> validator) {
        this.validators.put(Primitives.wrap(clazz), (Validator<Object>) validator);
        return this;
    }

    @Override
    public Map<Class<?>, Validator<Object>> getValidators() {
        return validators;
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
    public Commander registerCommandPostProcessor(BiConsumer<CommandContext, Object> postProcessor) {
        postProcessors.add(postProcessor);
        return this;
    }

    @Override
    public void executeCommand(CoreCommandSender sender, String label, String[] args) throws CommandParseException {
        CommandInfo commandInfo = commandMap.get(label);

        if (args == null) {
            args = new String[]{};
        }

        if (commandInfo.isParentCommand()) {
            if (args.length == 0) {
                throw new CommandParseException("No subcommand specified");
            }
            String sub = args[0];
            commandInfo = commandInfo.getSubCommand(sub);
            if (commandInfo == null) {
                throw new CommandNotFoundException("Could not find subcommand for " + sub);
            }
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            args = newArgs;
        } else if (commandInfo == null) {
            throw new CommandNotFoundException("Could not find command handler for " + label);
        }


        String[] argsCopy = new String[args.length];
        System.arraycopy(args, 0, argsCopy, 0, args.length);
        //List<String> argsList = ArgumentParser.combineMultiWordArguments(Arrays.asList(args));
        List<String> argsList = new ArrayList<>(Arrays.asList(args));

        CommandArgs cArgs = new CommandArgs(this, args, commandInfo.hasSwitches() ? extractSwitches(argsList, commandInfo.getParameters()) : null, commandInfo.hasFlags() ? extractFlags(argsList, commandInfo.getParameters()) : null, argsList);

        CommandContext context = new CommandContext(commandInfo, label.toLowerCase(), argsCopy, sender, cArgs);
        try {
            for (Consumer<CommandContext> preProcessor : preProcessors) {
                preProcessor.accept(context);
            }
            if (commandInfo.getPermission() != null && !sender.hasPermission(commandInfo.getPermission())) {
                throw new CommandParseException("You do not have permission to use this command.");
            }

            Object[] arguments = ArgumentParser.parseArguments(context, cArgs);

            Object result = null;
            try {
                result = context.getCommandInfo().getMethod().invoke(context.getCommandInfo().getInstance(), arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            for (BiConsumer<CommandContext, Object> postProcessor : postProcessors) {
                postProcessor.accept(context, result);
            }
        } catch (CommandException e) {
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
                } else {
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

    @Override
    public Map<String, CommandInfo> getCommandMap() {
        return commandMap;
    }

    @Override
    public CommandInfo getCommand(String label) {
        return commandMap.get(label.toLowerCase());
    }

    @Override
    public List<String> getSuggestions(CoreCommandSender sender, final String input) {
        int prefixLength = platform.getPrefix().length();
        final String s = input;
        //get the first word seperated by spaces without using split
        int spaceIndex = input.indexOf(' ');
        String in = spaceIndex == -1 ? input : input.substring(0, spaceIndex);

        if (in.startsWith(platform.getPrefix())) {
            in = in.substring(prefixLength);
        }
        boolean starts = in.startsWith(platform.getPrefix());
        String cmd = starts ? in.substring(prefixLength) : in;
        CommandInfo command = getCommand(cmd);
        String rest = s.substring(in.length() + (starts ? platform.getPrefix().length() : 0)).trim();
        if (command == null) {
            return null;
        }
        boolean subCommand = false;
        if (command.isParentCommand()) {
            subCommand = true;
            int spIndex = rest.indexOf(' ');
            String sub = spIndex == -1 ? rest : rest.substring(0, spIndex);
            command = command.getSubCommand(sub);
            if (command == null) {
                return null;
            }
        }


        List<String> suggestions = new ArrayList<>();
        ParameterInfo[] parameters = command.getParameters();
        //Count the spaces in rest
        int currentArgument = (int) rest.chars().filter(c -> c == (int) ' ').count();
        if (currentArgument == -1) {
            currentArgument = 0;
        }
        try {
            ParameterInfo param = parameters[currentArgument];
            Provider<?> provider = param.getProvider();
            if (provider != null) {
                List<String> suggestionsProvided = provider.provideSuggestions(input);
                if (suggestionsProvided == null) {
                    return null;
                }
                suggestions.addAll(suggestionsProvided);
            }
            return suggestions;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(CoreCommandSender sender, String prefix, String[] args) {
        String full = prefix + String.join(" ", args); //Avoiding String#split at all costs because it compiles regex
        return getSuggestions(sender, full);
    }
}
