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

package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.lang.LocalizedCommandException;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.sender.CoreCommandSender;

public class TestPlatform implements CommanderPlatform {

    @Override
    public void handleMessage(CommandContext context, String message, CoreCommandSender sender) {
        System.out.println(message);
    }

    @Override
    public void handleCommandException(CommandContext ctx, CommandException e) {
        if (e instanceof LocalizedCommandException)
            handleLocale((LocalizedCommandException) e, ctx.getCommandInfo().getCommander());
        else e.printStackTrace();
    }

    @Override
    public void handleCommandException(CommandInfo info, CoreCommandSender sender, CommandException e) {
        if (e instanceof LocalizedCommandException) handleLocale((LocalizedCommandException) e, info.getCommander());
        else e.printStackTrace();
    }

    public void handleLocale(LocalizedCommandException e, Commander commander) {
        System.err.println(commander.getResponseHandler().getMessage(e, e.getPlaceholders()));
    }

    @Override
    public void registerCommand(CommandInfo command) {
        //System.out.println("Registered command: " + command.getName());
    }

    @Override
    public void updateCommandAliases(CommandInfo commandInfo) {

    }

    @Override
    public HelpService getHelpService() {
        return new HelpService() {
            @Override
            public void sendHelp(CommandContext ctx, CoreCommandSender sender) {
                System.out.println("Help Service 1");
            }

            @Override
            public void sendHelp(CommandInfo info, CoreCommandSender sender) {
                System.out.println("Help Service 2");
            }
        };
    }

    @Override
    public void runAsync(Runnable runnable) {
        new Thread(runnable, "Commander Async").start();
    }
}
