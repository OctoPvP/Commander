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

import net.octopvp.commander.annotation.Range;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandParseException;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.Deque;
import java.util.List;

public class ByteProvider implements Provider<Byte> {
    @Override
    public Byte provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        try {
            String arg = args.poll();
            return Byte.parseByte(arg);
        } catch (NumberFormatException e) {
            throw new InvalidArgsException(commandInfo);
        }
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return null;
    }

    @Override
    public Byte provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getParameter().isAnnotationPresent(Range.class)) return (byte) parameterInfo.getParameter().getAnnotation(Range.class).defaultValue();
        return -1;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class[]{byte.class};
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
