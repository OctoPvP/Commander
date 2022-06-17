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

package net.octopvp.commander.provider.impl;

import net.octopvp.commander.annotation.Duration;
import net.octopvp.commander.annotation.Range;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandParseException;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.commander.util.CommanderUtilities;

import java.util.Deque;
import java.util.List;

public class LongProvider implements Provider<Long> {
    @Override
    public Long provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        String arg = args.poll();
        if (parameterInfo.getParameter().isAnnotationPresent(Duration.class)) {
            Duration duration = parameterInfo.getParameter().getAnnotation(Duration.class);
            if (arg.equalsIgnoreCase("perm") || arg.equalsIgnoreCase("permanent")) {
                if (!duration.allowPermanent()) {
                    throw new CommandParseException("Permanent is not allowed for this parameter.");
                }
                return -1L;
            }
            try {
                return CommanderUtilities.parseTime(arg == null || arg.isEmpty() ? duration.defaultValue() : arg, duration.future());
            } catch (Exception e) {
                throw new CommandParseException(e);
            }
        }
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            throw new InvalidArgsException(commandInfo);
        }
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return null;
    }

    @Override
    public Long provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getParameter().isAnnotationPresent(Duration.class)) {
            Duration duration = parameterInfo.getParameter().getAnnotation(Duration.class);
            try {
                return CommanderUtilities.parseTime(duration.defaultValue(), duration.future());
            } catch (Exception e) {
                throw new CommandParseException(e);
            }
        }
        if (parameterInfo.getParameter().isAnnotationPresent(Range.class))
            return (long) parameterInfo.getParameter().getAnnotation(Range.class).defaultValue();
        return -1L;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class<?>[]{long.class};
    }

    @Override
    public boolean failOnException() {
        return true;
    }

    @Override
    public boolean failOnExceptionIgnoreOptional() {
        return true;
    }
}
