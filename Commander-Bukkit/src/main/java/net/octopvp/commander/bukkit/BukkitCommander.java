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

package net.octopvp.commander.bukkit;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.bukkit.annotation.ConsoleOnly;
import net.octopvp.commander.bukkit.annotation.OpOnly;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.commander.bukkit.impl.BukkitCommandSenderImpl;
import net.octopvp.commander.bukkit.impl.BukkitHelpService;
import net.octopvp.commander.bukkit.providers.CommandSenderProviders;
import net.octopvp.commander.bukkit.providers.OfflinePlayerProvider;
import net.octopvp.commander.bukkit.providers.PlayerProvider;
import net.octopvp.commander.lang.LocalizedCommandException;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ResourceBundle;

public class BukkitCommander {
    public static BukkitHelpService HELP_SERVICE = BukkitHelpService.INSTANCE;
    public static Commander getCommander(Plugin plugin) {
        BukkitPlatform platform = new BukkitPlatform(plugin);
        Commander impl = new CommanderImpl(platform)
                .init()
                .registerProvider(Player.class, new PlayerProvider())
                .registerProvider(OfflinePlayer.class, new OfflinePlayerProvider())
                .registerProvider(CommandSender.class, new CommandSenderProviders.CommandSenderProvider())
                .registerProvider(BukkitCommandSender.class, new CommandSenderProviders.BukkitCoreCommandSenderProvider())
                .registerProvider(BukkitCommandSenderImpl.class, new CommandSenderProviders.BukkitCoreCommandSenderImplProvider())
                .registerProvider(CoreCommandSender.class, new CommandSenderProviders.CoreCommandSenderProvider())
                .registerCommandPreProcessor(ctx -> {
                    if (ctx.getCommandInfo().isAnnotationPresent(PlayerOnly.class)) {
                        BukkitCommandSender sender = (BukkitCommandSender) ctx.getCommandSender();
                        if (!sender.isPlayer()) {
                            throw new LocalizedCommandException("must-be.player");
                        }
                    }else if (ctx.getCommandInfo().isAnnotationPresent(ConsoleOnly.class)) {
                        BukkitCommandSender sender = (BukkitCommandSender) ctx.getCommandSender();
                        if (!sender.isConsole()) {
                            throw new LocalizedCommandException("must-be.console");
                        }
                    } else if (ctx.getCommandInfo().isAnnotationPresent(OpOnly.class)) {
                        BukkitCommandSender sender = (BukkitCommandSender) ctx.getCommandSender();
                        if (!sender.isOp()) {
                            throw new LocalizedCommandException("must-be.op");
                        }
                    }
                });
        platform.setCommander(impl);
        impl.getResponseHandler().addBundle(ResourceBundle.getBundle("bukkit", impl.getResponseHandler().getLocale()));
        return impl;
    }
}
