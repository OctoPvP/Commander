package net.octopvp.commander;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.config.CommanderConfig;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.commander.validator.Validator;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Commander {

    Commander init();

    Commander register(Object... objects);

    Commander registerPackage(String packageName);

    CommanderConfig getConfig();

    Commander setConfig(CommanderConfig config);

    Map<Class<?>,Provider<?>> getArgumentProviders();

    Commander registerProvider(Class<?> type,Provider<?> provider);

    Commander removeProvider(Class<?> clazz);

    <T> Commander registerValidator(Class<T> clazz, Validator<T> validator);

    Map<Class<?>,Validator<Object>> getValidators();

    CommanderPlatform getPlatform();

    <T> Commander registerDependency(Class<T> type, Supplier<T> supplier);

    <T> Commander registerDependency(Class<T> type, T val);

    Map<Class<?>, Supplier<?>> getDependencies();

    Map<String, CommandInfo> getCommandMap();

    Commander registerCommandPreProcessor(Consumer<CommandContext> preProcessor);

    Commander registerCommandPostProcessor(BiConsumer<CommandContext, Object> postProcessor);

    CommandInfo getCommand(String label);

    void executeCommand(CoreCommandSender sender, String label, String[] args) throws CommandException;

    List<String> getSuggestions(CoreCommandSender sender, String command);

    List<String> getSuggestions(CoreCommandSender sender, String prefix, String[] args);
}
