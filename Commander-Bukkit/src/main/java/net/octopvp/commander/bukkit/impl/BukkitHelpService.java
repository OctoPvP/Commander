/*
 * Copyright (c) Evan Yu 2024.
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

import net.octopvp.commander.annotation.Hidden;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.ChatColor;

public class BukkitHelpService implements HelpService {
    public static BukkitHelpService INSTANCE = new BukkitHelpService();

    protected BukkitHelpService(){} // We only need one instance of this class

    @Override
    public void sendHelp(CommandContext ctx, CoreCommandSender sender) {
        CommandInfo info = ctx.getCommandInfo();
        sendHelp(info,sender);
    }

    @Override
    public void sendHelp(CommandInfo info, CoreCommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + String.valueOf(ChatColor.STRIKETHROUGH) + "--------------------------------");
        sender.sendMessage(ChatColor.AQUA + "Help for " + ChatColor.GOLD + info.getName() + ChatColor.GRAY + " - " + info.getDescription());
        if (info.isParentCommand()) {
            for (CommandInfo subCommand : info.getSubCommands()) {
                if (subCommand.getMethod().isAnnotationPresent(Hidden.class)) {
                    continue;
                }
                sender.sendMessage(ChatColor.GRAY + " " + subCommand.getFullUsage() + (subCommand.getDescription() != null || subCommand.getDescription().isEmpty() ? ChatColor.GRAY + " - " + subCommand.getDescription() : ""));
            }
        }
        sender.sendMessage(ChatColor.GRAY + String.valueOf(ChatColor.STRIKETHROUGH) + "--------------------------------");
    }
}
