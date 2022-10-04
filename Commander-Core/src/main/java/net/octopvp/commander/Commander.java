/*
 * Copyright (c) Badbird5907 2022.
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

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.config.CommanderConfig;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.lang.ResponseHandler;
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

    Map<Class<?>, Provider<?>> getArgumentProviders();

    Commander registerProvider(Class<?> type, Provider<?> provider);

    Commander removeProvider(Class<?> clazz);

    <T> Commander registerValidator(Class<T> clazz, Validator<T> validator);

    Map<Class<?>, Validator<Object>> getValidators();

    ResponseHandler getResponseHandler();

    CommanderPlatform getPlatform();

    <T> Commander registerDependency(Class<T> type, Supplier<T> supplier);

    <T> Commander registerDependency(Class<T> type, T val);

    Map<Class<?>, Supplier<?>> getDependencies();

    Map<String, CommandInfo> getCommandMap();

    Commander registerCommandPreProcessor(Consumer<CommandContext> preProcessor);

    Commander registerCommandPostProcessor(BiConsumer<CommandContext, Object> postProcessor);

    CommandInfo getCommand(String label);

    void executeCommand(CoreCommandSender sender, String label, String[] args) throws CommandException;

    List<String> getSuggestions(CoreCommandSender sender, String command, Object senderWrapper);

    List<String> getSuggestions(CoreCommandSender sender, String prefix, String[] args, Object senderWrapper);
}
