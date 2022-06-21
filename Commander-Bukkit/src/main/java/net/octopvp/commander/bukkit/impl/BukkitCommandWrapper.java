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

package net.octopvp.commander.bukkit.impl;

import net.octopvp.commander.command.CommandInfo;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class BukkitCommandWrapper extends org.bukkit.command.Command {
    private CommandInfo commandInfo;

    public BukkitCommandWrapper(CommandInfo command) {
        super(command.getName(), command.getDescription(), command.getUsage(), Arrays.asList(command.getAliases()));
        this.commandInfo = command;
    }

    //strip the plugin:command from the command if it exists
    private static final Pattern PLUGIN_PREFIX_PATTERN = Pattern.compile("^(?:[a-zA-Z0-9_]*:)?([a-zA-Z0-9_]+)");
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        String label = PLUGIN_PREFIX_PATTERN.matcher(commandLabel).replaceFirst("$1");
        BukkitCommandSenderImpl cmdSenderImpl = new BukkitCommandSenderImpl(sender);
        commandInfo.getCommander().executeCommand(cmdSenderImpl, label, args);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return commandInfo.getCommander().getSuggestions(new BukkitCommandSenderImpl(sender), alias, args);
    }
}
