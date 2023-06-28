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

package net.octopvp.commander.platform;

import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.sender.CoreCommandSender;
import org.reflections.Reflections;

import java.util.Collection;

public interface CommanderPlatform {
    void handleMessage(String message, CoreCommandSender sender);

    void handleError(String error, CoreCommandSender sender);

    void handleCommandException(CommandContext ctx, CommandException e);

    void handleCommandException(CommandInfo info, CoreCommandSender sender, CommandException e);

    /**
     * Handle an exception that occurred during command execution
     *
     * @param ctx
     * @param e
     * @param sender
     * @return true if the exception should be propagated
     */
    default boolean handleExecutionException(CommandContext ctx, Exception e, CoreCommandSender sender) {
        return true;
    }

    default boolean hasPermission(CoreCommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    void registerCommand(CommandInfo command);

    void updateCommandAliases(CommandInfo commandInfo);

    default boolean isSenderParameter(ParameterInfo parameterInfo) {
        return parameterInfo.getParameter().isAnnotationPresent(Sender.class) ||
                parameterInfo.getParameter().getName().equalsIgnoreCase("sender") ||
                parameterInfo.getParameter().getType().equals(CoreCommandSender.class);
    }

    default String getPrefix() {
        return "/";
    }

    HelpService getHelpService();

    void runAsync(Runnable runnable);

    default Collection<Class<?>> getClassesInPackage(String packageName) {
        return getReflections(packageName).getSubTypesOf(Object.class);
    }

    default Reflections getReflections(String packageName) {
        return new Reflections(packageName);
    }
}
