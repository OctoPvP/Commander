package net.octopvp.commander;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.DistributeOnMethods;
import net.octopvp.commander.annotation.DontAutoInit;
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
import net.octopvp.commander.provider.impl.*;
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
import java.util.stream.Collectors;

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
        registerProvider(Long.class, new LongProvider());
        registerProvider(Double.class, new DoubleProvider());
        registerProvider(Byte.class, new ByteProvider());
        registerProvider(Boolean.class, new BooleanProvider());
        registerProvider(Short.class, new ShortProvider());
        registerProvider(CoreCommandSender.class, new SenderProvider());
        registerProvider(String.class, new StringProvider());
        registerProvider(String[].class, new StringArrayProvider());
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
            if (object instanceof Collection) {
                Collection<Object> objs = (Collection<Object>) object;
                for (Object o : objs) {
                    registerCmd(o);
                }
                continue;
            }
            registerCmd(object);
        }
        return this;
    }

    private void registerCmd(Object object) {
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
            if (classHasMainCommand) {
                commandInfo.setSubCommand(true);
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

    @Override
    public Commander registerPackage(String packageName) {
        register(platform.getClassesInPackage(packageName).stream().filter(clazz -> {
            if (clazz.isAnnotationPresent(DontAutoInit.class)) return false;
            return true;
        }).map(clazz -> {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
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

        try {
            if (commandInfo.isParentCommand()) {
                final CommandInfo parent = commandInfo;
                if (args.length == 0) {
                    commandInfo = commandInfo.getSubCommand(""); //this might need some work...
                    if (commandInfo == null) {
                        //throw new CommandParseException("No subcommand specified");
                        getPlatform().getHelpService().sendHelp(parent, sender);
                        return;
                    }
                }
                boolean isRootLevel = false;
                String sub = args[0];
                commandInfo = commandInfo.getSubCommand(sub);
                if (commandInfo == null) {
                    if (sub.equalsIgnoreCase("help")) {
                        getPlatform().getHelpService().sendHelp(parent, sender);
                        return;
                    }
                    commandInfo = parent.getSubCommand("");
                    isRootLevel = commandInfo != null;
                    if (!isRootLevel) {
                        throw new CommandNotFoundException("Could not find subcommand \"" + sub + "\" for command " + parent.getName());
                    }
                }
                if (!isRootLevel) {
                    String[] newArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                    args = newArgs;
                }
            } else if (commandInfo == null) {
                throw new CommandNotFoundException("Could not find command handler for " + label);
            }


            String[] argsCopy = new String[args.length];
            System.arraycopy(args, 0, argsCopy, 0, args.length);
            //List<String> argsList = ArgumentParser.combineMultiWordArguments(Arrays.asList(args));
            List<String> argsList;
            if (config.isJoinArgsWithQuotes()) {
                argsList = ArgumentParser.combineArgs(Arrays.asList(args));
            } else {
                argsList = new ArrayList<>(Arrays.asList(args));
            }

            CommandArgs cArgs = new CommandArgs(this, args, commandInfo.hasSwitches() ? extractSwitches(argsList, commandInfo.getParameters()) : null, commandInfo.hasFlags() ? extractFlags(argsList, commandInfo.getParameters()) : null, argsList);

            CommandContext context = new CommandContext(commandInfo, label.toLowerCase(), argsCopy, sender, cArgs);
            try {
                for (Consumer<CommandContext> preProcessor : preProcessors) {
                    preProcessor.accept(context);
                }

                if (commandInfo.isSubCommand()) {
                    if (commandInfo.getPermission() != null && !sender.hasPermission(commandInfo.getPermission())) {
                        throw new NoPermissionException();
                    }
                    if (commandInfo.getParent().getPermission() != null && !sender.hasPermission(commandInfo.getParent().getPermission()))
                        throw new NoPermissionException();
                } else if (commandInfo.getPermission() != null && !sender.hasPermission(commandInfo.getPermission())) {
                    throw new NoPermissionException();
                }

                Object[] arguments = ArgumentParser.parseArguments(context, cArgs);

                if (arguments == null) return;

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
        } catch (CommandException e) {
            platform.handleCommandException(commandInfo, sender, e);
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
        System.out.println("Getting suggestions for " + input);
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
        String rest = s.substring(in.length() + (starts ? platform.getPrefix().length() : 0));
        if (command == null) {
            return null;
        }
        if (command.isParentCommand()) {
            rest = rest.trim(); // fix indexOf below
            int spIndex = rest.indexOf(' ');
            String sub = spIndex == -1 ? rest : rest.substring(0, spIndex);
            command = command.getSubCommand(sub);
            if (command == null) {
                return null;
            }
        }

        List<String> suggestions = new ArrayList<>();
        ParameterInfo[] parameters =
                command.getParametersExcludingSender();
                //command.getParameters();
        //Count the spaces in rest
        int currentArgument = (int) rest.chars().filter(c -> c == (int) ' ').count();
        if (currentArgument == -1) {
            currentArgument = 0;
        }
        if (currentArgument >= parameters.length) {
            return null;
        }
        ParameterInfo param = parameters[currentArgument];
        Provider<?> provider = param.getProvider();
        if (provider != null) {
            List<String> suggestionsProvided = provider.provideSuggestions(input,sender);
            if (suggestionsProvided == null) {
                return null;
            }
            suggestions.addAll(suggestionsProvided);
        }
        return suggestions;
    }

    @Override
    public List<String> getSuggestions(CoreCommandSender sender, String prefix, String[] args) {
        String full = prefix + " " + String.join(" ", args); //Avoiding String#split at all costs because it compiles regex
        List<String> suggestions = getSuggestions(sender, full);
        System.out.println("Suggestions: " + suggestions);
        return suggestions;
    }
}
