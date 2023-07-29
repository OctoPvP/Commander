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

package net.octopvp.commander;

import net.octopvp.commander.annotation.*;
import net.octopvp.commander.argument.ArgumentParser;
import net.octopvp.commander.argument.CommandArgs;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.CompleterInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.config.CommanderConfig;
import net.octopvp.commander.exception.*;
import net.octopvp.commander.lang.DefaultResponseHandler;
import net.octopvp.commander.lang.LocalizedCommandException;
import net.octopvp.commander.lang.ResponseHandler;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.provider.impl.*;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.commander.util.CommanderUtilities;
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
    private final CommanderPlatform platform;
    private final Map<Class<?>, Provider<?>> argumentProviders = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> dependencies = new HashMap<>();
    private final Map<String, CommandInfo> commandMap = new HashMap<>();
    private final List<Consumer<CommandContext>> preProcessors = new ArrayList<>();
    private final List<BiConsumer<CommandContext, Object>> postProcessors = new ArrayList<>();
    private final Map<Class<?>, Validator<Object>> validators = new HashMap<>();
    private final ResponseHandler responseHandler;
    private final Map<Method, Object> completerCache = new HashMap<>();
    private CommanderConfig config;

    public CommanderImpl(CommanderPlatform platform) {
        this(platform, new CommanderConfig());
    }

    public CommanderImpl(CommanderPlatform platform, CommanderConfig config) {
        this.platform = platform;
        this.config = config;
        Locale locale = config.getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (config.getResponseHandler() == null) {
            this.responseHandler = new DefaultResponseHandler(locale);
        } else {
            this.responseHandler = config.getResponseHandler();
        }
    }

    @Override
    public Commander register(Object... objects) {
        if (objects == null) return this;
        for (Object object : objects) {
            if (object == null) {
                continue;
            }
            if (object instanceof Collection) {
                Collection<Object> objs = (Collection<Object>) object;
                for (Object o : objs) {
                    registerCmd(o);
                }
                continue;
            }
            registerCmd(object);
        }
        registerCompleters();
        return this;
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
        registerProvider(CommandInfo.class, new CommandInfoProvider());
        registerCommandPreProcessor(context -> {
            CommandInfo commandInfo = context.getCommandInfo();
            CoreCommandSender sender = context.getCommandSender();
            if (commandInfo.isSubCommand()) {
                if (commandInfo.getPermission() != null && !sender.hasPermission(commandInfo.getPermission())) {
                    throw new NoPermissionException();
                }
                if (commandInfo.getParent().getPermission() != null && !sender.hasPermission(commandInfo.getParent().getPermission()))
                    throw new NoPermissionException();
            } else if (commandInfo.getPermission() != null && !sender.hasPermission(commandInfo.getPermission())) {
                throw new NoPermissionException();
            }
        });
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
            boolean passed = range != null && (value.doubleValue() > range.max() || value.doubleValue() < range.min());
            //System.out.println("Passed: " + passed + " | " + (range != null) + " | " + (value.doubleValue() > range.max()) + " | " + (value.doubleValue() < range.min()));
            if (passed) {
                if (parameter.getParameter().isAnnotationPresent(DefaultNumber.class) && value.doubleValue() == parameter.getParameter().getAnnotation(DefaultNumber.class).value()) { //All number providers should use Range#defaultValue() if the annotation is present
                    return; //TODO we gotta find a better way to handle primitives, which cant take null
                }
                StringBuilder minMax = new StringBuilder("(");
                if (range.min() != Double.MIN_VALUE) {
                    String min = String.valueOf(range.min());
                    if (min.endsWith(".0")) {
                        min = min.substring(0, min.length() - 2);
                    }
                    minMax.append("min: ").append(min);
                }
                if (range.max() != Double.MAX_VALUE) {
                    if (minMax.length() > 1) {
                        minMax.append(" | ");
                    }
                    String max = String.valueOf(range.max());
                    if (max.endsWith(".0")) {
                        max = max.substring(0, max.length() - 2);
                    }
                    minMax.append("max: ").append(max);
                }
                minMax.append(")");
                String finalMinMax = minMax.toString();
                if (finalMinMax.equals("()")) {
                    finalMinMax = "";
                }
                //System.out.println("Final: " + finalMinMax);
                throw new ValidateException("validate.exception", value.doubleValue(), finalMinMax);
            }
        });
        return this;
    }

    private void registerCmd(Object object) {
        try {
            if (object == null) return;
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
                parentInfo = commandMap.get(parent.name().toLowerCase());
                if (parentInfo == null)
                    parentInfo = new CommandInfo(null, parent.name(), parent.description(), parent.usage(), parent.aliases(), null, object, annotations, this);
                parentInfo.setParentCommand(true);
                if (parentInfo.getSubCommands() == null) parentInfo.setSubCommands(new ArrayList<>());
                commandMap.put(parent.name().toLowerCase(), parentInfo);
                for (String alias : parent.aliases()) {
                    commandMap.put(alias.toLowerCase(), parentInfo);
                }
                if (object.getClass().isAnnotationPresent(SecondaryParent.class)) {
                    if (parentInfo.isRegisteredWithPlatform()) {
                        getPlatform().updateCommandAliases(parentInfo);
                    }
                } else {
                    platform.registerCommand(parentInfo);
                    parentInfo.setRegisteredWithPlatform(true);
                }
            }
            for (Method method : object.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
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
                } else if (method.isAnnotationPresent(Completer.class)) {
                    completerCache.put(method, object);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerCompleters() {
        Iterator<Map.Entry<Method, Object>> iterator = completerCache.entrySet().iterator();
        while (iterator.hasNext()) {
            try {
                Map.Entry<Method, Object> entry = iterator.next();
                iterator.remove();
                Method method = entry.getKey();
                Completer completer = method.getAnnotation(Completer.class);
                String targetCommand = completer.name();
                boolean isInParentCommand = method.getDeclaringClass().isAnnotationPresent(Command.class);
                Command parentCommand = method.getDeclaringClass().getAnnotation(Command.class);
                int[] indexes = completer.index();

                for (int index : indexes) {
                    if (index < -1) {
                        throw new IllegalArgumentException("Completer index must be greater than -1!"); // -1 is the catch all
                    }
                }
                if (targetCommand.equals("")) {
                    throw new IllegalArgumentException("Completer name cannot be empty!");
                }
                String[] split = targetCommand.toLowerCase().split(" ");
                boolean isSubCommand = isInParentCommand || split.length > 1;

                CommandInfo command, parent;
                if (!isSubCommand) command = getCommand(split[0]);
                else command = getCommand(parentCommand.name());
                if (command == null) return;

                if (isSubCommand && !command.isParentCommand())
                    throw new IllegalArgumentException("Completer references a subcommand, where the command is not a parent command!");
                if (!isSubCommand && command.isParentCommand())
                    throw new IllegalArgumentException("Completer references a parent command, which cannot be tab-completed!");

                CompleterInfo completerInfo;
                if (isSubCommand) {
                    parent = command;
                    if (split.length > 1) {
                        command = parent.getSubCommand(split[split.length - 1]);
                    } else if (split.length == 1) {
                        command = parent.getSubCommand(split[0]);
                    }
                    if (command == null)
                        throw new IllegalArgumentException("Completer references a subcommand, which does not exist!");
                    completerInfo = new CompleterInfo(
                            command,
                            entry.getValue(),
                            method,
                            completer,
                            parent
                    );
                } else {
                    completerInfo = new CompleterInfo(
                            command,
                            entry.getValue(),
                            method,
                            completer
                    );
                }
                if (indexes.length == 0) {
                    command.getCompleters().put(-1, completerInfo); //This means the completer will be called for every parameter, not recommended but it's there if you want it
                } else {
                    for (int index : indexes) {
                        command.getCompleters().put(index, completerInfo);
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to register completer!");
                e.printStackTrace();
            }
        }

        completerCache.clear(); //try to remove any leftover completers in case of an exception/concurrent modification
    }

    @Override
    public Commander registerPackage(String packageName) {
        register(platform.getClassesInPackage(packageName).stream().filter(clazz -> !clazz.isAnnotationPresent(DontAutoInit.class)).map(clazz -> {
            try {
                if (clazz.isEnum()) return null;
                boolean isCommandClass = false;
                for (Method declaredMethod : clazz.getDeclaredMethods()) {
                    if (declaredMethod.isAnnotationPresent(Command.class)) {
                        isCommandClass = true;
                        break;
                    }
                }
                if (!isCommandClass) isCommandClass = clazz.isAnnotationPresent(Command.class);

                if (!isCommandClass) return null;

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
    public ResponseHandler getResponseHandler() {
        return responseHandler;
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
        try {
            if (commandInfo == null) {
                throw new CommandNotFoundException("handler.not-found", label.toLowerCase());
            }
            if (commandInfo.isAsync()) {
                getPlatform().runAsync(() -> executeInternally(sender, label, args, commandInfo));
            } else executeInternally(sender, label, args, commandInfo);
        } catch (CommandException e) {
            LocalizedCommandException.checkResponseHandlerNull(e, getResponseHandler());
            platform.handleCommandException(commandInfo, sender, e);
        }
    }

    private void executeInternally(CoreCommandSender sender, String label, String[] args, CommandInfo commandInfo) {
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
                boolean isRootLevel = args.length == 0;
                if (isRootLevel) {
                    commandInfo = parent.getSubCommand("");
                    if (commandInfo == null) {
                        getPlatform().getHelpService().sendHelp(parent, sender);
                        return;
                    }
                } else {
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
                            throw new CommandNotFoundException("subcommand.not-found", sub.toLowerCase(), parent.getName());
                        }
                    }
                    String[] newArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                    args = newArgs;
                }
            } else if (commandInfo == null) {
                throw new CommandNotFoundException("handler.not-found", label.toLowerCase());
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

                Object[] arguments = ArgumentParser.parseArguments(context, cArgs);

                if (arguments == null) return;

                Object result = null;
                try {
                    result = context.getCommandInfo().getMethod().invoke(context.getCommandInfo().getInstance(), arguments);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    LocalizedCommandException.checkResponseHandlerNull(e, getResponseHandler());
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    if (e.getCause() != null) {
                        if (e.getCause() instanceof CommandException) {
                            LocalizedCommandException.checkResponseHandlerNull((Exception) e.getCause(), getResponseHandler());
                            platform.handleCommandException(context, (CommandException) e.getCause());
                        } else {
                            platform.handleExecutionException(context, e, sender);
                            throw e;
                        }
                    }
                }
                for (BiConsumer<CommandContext, Object> postProcessor : postProcessors) {
                    postProcessor.accept(context, result);
                }
            } catch (CommandException e) {
                LocalizedCommandException.checkResponseHandlerNull(e, getResponseHandler());
                if (e instanceof MessageException) {
                    MessageException me = (MessageException) e;
                    getPlatform().handleMessage(me.getMessage(), sender);
                    return;
                }
                platform.handleCommandException(context, e);
            } catch (Exception e) {
                System.err.println("An error occurred while executing command \"" + label + "\"");
                LocalizedCommandException.checkResponseHandlerNull(e, getResponseHandler());
                e.printStackTrace();
            }
        } catch (CommandException e) {
            LocalizedCommandException.checkResponseHandlerNull(e, getResponseHandler());
            platform.handleCommandException(commandInfo, sender, e);
        }

    }

    private Map<String, String> extractFlags(final List<String> args, final ParameterInfo[] params) {
        List<ParameterInfo> paramsList = Arrays.asList(params);
        Map<String, String> flags = new HashMap<>();
        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            boolean matchDouble = config.isMatchDoubleFlagAndSwitch() && arg.startsWith(CommanderUtilities.repeat(config.getFlagPrefix(), 2));
            if (arg.startsWith(config.getFlagPrefix()) || matchDouble) {
                String flag = arg.substring(config.getFlagPrefix().length() + (matchDouble ? 1 : 0));
                if (flags.containsKey(flag)) {
                    throw new CommandParseException("flags.multiple", flag);
                }
                if (paramsList.stream().noneMatch(p -> p.isFlag() && p.getFlags().contains(flag))) {
                    continue;
                }
                iterator.remove();
                if (iterator.hasNext()) {
                    String value = iterator.next();
                    iterator.remove();
                    flags.put(flagAliasToName(flag, params), value);
                } else {
                    throw new CommandParseException("flags.requires-value", flag);
                }
            }
        }
        return flags;
    }

    private String flagAliasToName(String alias, ParameterInfo[] params) { // TODO this is a hot fix, need to properly implement a faster solution
        for (ParameterInfo param : params) {
            if (param.isFlag() && param.getFlags().contains(alias)) {
                return param.getParameter().getAnnotation(Flag.class).value();
            }
        }
        return alias; // likely not a alias then
    }
    private Map<String, Boolean> extractSwitches(final List<String> args, final ParameterInfo[] params) {
        List<ParameterInfo> paramsList = Arrays.asList(params);
        Map<String, Boolean> switches = new HashMap<>();
        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            boolean matchDouble = config.isMatchDoubleFlagAndSwitch() && arg.startsWith(CommanderUtilities.repeat(config.getSwitchPrefix(), 2));
            if (arg.startsWith(config.getSwitchPrefix()) || matchDouble) {
                String commandSwitch = arg.substring(config.getSwitchPrefix().length() + (matchDouble ? 1 : 0));
                if (switches.containsKey(commandSwitch)) {
                    throw new CommandParseException("switches.multiple", commandSwitch);
                }
                if (paramsList.stream().noneMatch(p -> p.isSwitch() && p.getSwitches().contains(commandSwitch))) {
                    continue;
                }
                iterator.remove();
                switches.put(commandSwitch, true);

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
    public List<String> getSuggestions(CoreCommandSender sender, final String input, Object senderWrapper) {
        String[] split = input.split(" ");
        if (split.length == 0) {
            return null;
        }

        String label = split[0];
        if (label.startsWith(getPlatform().getPrefix())) label = label.substring(getPlatform().getPrefix().length());

        CommandInfo command = getCommand(label), parent = null;
        if (command == null) {
            return null;
        }
        if (command.isParentCommand()) {
            parent = command;
            if (split.length == 1) {
                List<String> list = parent.getSubCommands().stream().map(CommandInfo::getName).collect(Collectors.toList());
                if (!list.contains("help"))
                    list.add("help");
                return list;
            } else if (split.length == 2 && !input.endsWith(" ")) {
                List<String> suggestions = parent.getSubCommands().stream().map(CommandInfo::getName).collect(Collectors.toList());
                if (!suggestions.contains("help"))
                    suggestions.add("help");
                suggestions.removeIf(s -> !s.trim().toLowerCase().startsWith(split[1].trim().toLowerCase()));
                return suggestions;
            }
            command = parent.getSubCommand(split[1]);
            if (command == null) {
                return null;
            }
        }

        if (config.isCheckPermissionsOnSuggestion() && command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
            return null;
        }

        ParameterInfo[] params = command.getCommandParameters();

        int diff = parent == null ? 1 : 2;
        int index = split.length - diff;
        String lastArg = split[split.length - 1];

        if (!input.endsWith(" ") && config.isShowNextSuggestionOnlyIfEndsWithSpace()) index--;

        //remove switches and flags from the index
        for (int i = 0; i < index; i++) {
            if (split[i].startsWith(config.getFlagPrefix()) || config.isMatchDoubleFlagAndSwitch() && split[i].startsWith(CommanderUtilities.repeat(config.getFlagPrefix(), 2))) {
                index--;
            } else if (split[i].startsWith(config.getSwitchPrefix()) || config.isMatchDoubleFlagAndSwitch() && split[i].startsWith(CommanderUtilities.repeat(config.getSwitchPrefix(), 2))) {
                index--;
            }
        }
        if (index >= params.length || index < 0) {
            return null;
        }

        CompleterInfo customCompleter = command.getCompleters().get(index);
        boolean allParams = false;
        if (customCompleter == null) {
            customCompleter = command.getCompleters().get(-1);
            allParams = true;
        }
        Collection<String> customReturn = null;
        if (customCompleter != null) {
            Method method = customCompleter.getMethod();
            Object[] args = ArgumentParser.parseCompleterArguments(customCompleter, command, sender, method.getParameters(), input, label, lastArg, split, allParams ? -1 : index, senderWrapper);
            if (args == null) return null;
            try {
                Object result = method.invoke(customCompleter.getInstance(), args);
                if (result instanceof Collection) {
                    customReturn = (Collection<String>) result;
                } else {
                    throw new SuggestionException("suggestion.completer-must-return-string");
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                throw new SuggestionException("suggestion.failed-to-invoke");
            }
        }

        Collection<String> suggestionsProvided;
        if (customReturn == null) {
            ParameterInfo param = null;
            boolean found = false;
            while (!found) {
                param = params[index];
                if (param.isFlag() || param.isSwitch()) { //TODO add support for flag and switch suggestions
                    if (++index >= params.length) {
                        return null;
                    }
                    if (param.isFlag()) {
                        param = params[index];
                    }
                } else {
                    found = true;
                }
            }
            Provider<?> provider = param.getProvider();

            if (provider == null) {
                return null;
            }

            suggestionsProvided = provider.provideSuggestions(input, lastArg, sender);
            if (suggestionsProvided == null) {
                return null;
            }
        } else suggestionsProvided = customReturn;
        List<String> suggestions = new ArrayList<>(suggestionsProvided);
        if (config.isFilterSuggestions() && !input.endsWith(" ")) {
            suggestions.removeIf(s -> !s.trim().toLowerCase().startsWith(lastArg.trim().toLowerCase()));
        }
        return suggestions;

        /*
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
            System.out.println("Command not found");
            return null;
        }
        CommandInfo parent;
        if (command.isParentCommand()) {
            parent = command;
            rest = rest.trim(); // fix indexOf below
            int spIndex = rest.indexOf(' ');
            String sub = spIndex == -1 ? rest : rest.substring(0, spIndex);
            command = command.getSubCommand(sub);
            if (command == null || (command.getCommandParameters().length > 0)) {
                if (parent.getSubCommands() == null) {
                    System.out.println("No subcommands found");
                    return null;
                }
                System.out.println("Returning sub commands");
                return new ArrayList<>(Arrays.asList(parent.getSubCommands().stream().map(CommandInfo::getName).toArray(String[]::new)));
            }
        }
        List<String> suggestions = new ArrayList<>();

        ParameterInfo[] parameters =
                command.getCommandParameters();
                //command.getParameters();
        //Count the spaces in rest
        System.out.println("Rest: " + rest);
        int currentArgument = (int) rest.chars().filter(c -> c == (int) ' ').count();
        if (currentArgument == -1) {
            currentArgument = 0;
        }
        System.out.println("Current arg: " + currentArgument + " | params: " + Arrays.toString(parameters));
        if (currentArgument > parameters.length) {
            System.out.println("No more arguments");
            return null;
        }
        ParameterInfo param = parameters[currentArgument];
        Provider<?> provider = param.getProvider();
        System.out.println("Param #" + currentArgument + " | Param: " + param.getName() + " | Provider: " + provider);
        if (provider != null) {
            List<String> suggestionsProvided = provider.provideSuggestions(input,sender);
            if (suggestionsProvided == null) {
                System.out.println("No suggestions provided");
                return null;
            }
            suggestions.addAll(suggestionsProvided);
        }
        System.out.println("Suggestions for " + input + ": " + suggestions);
        return suggestions;
         */
    }

    @Override
    public List<String> getSuggestions(CoreCommandSender sender, String prefix, String[] args, Object wrapper) {
        String full = prefix + " " + String.join(" ", args); //Avoiding String#split at all costs because it compiles regex
        return getSuggestions(sender, full, wrapper);
    }
}
