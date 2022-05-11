package net.octopvp.commander;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.config.CommanderConfig;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Commander {

    Commander init();

    Commander register(Object... objects);

    CommanderConfig getConfig();

    Commander setConfig(CommanderConfig config);

    Collection<Provider<?>> getArgumentProviders();

    Commander registerProvider(Provider<?> provider);

    CommanderPlatform getPlatform();

    <T> Commander registerDependency(Class<T> type, Supplier<T> supplier);

    <T> Commander registerDependency(Class<T> type, T val);

    Map<Class<?>, Supplier<?>> getDependencies();

    Commander registerCommandPreProcessor(Consumer<CommandContext> preProcessor);

    void executeCommand(CoreCommandSender sender, String label, String[] args) throws Exception;
}
