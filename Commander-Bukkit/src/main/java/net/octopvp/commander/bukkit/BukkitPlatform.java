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

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.Commander;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.impl.BukkitCommandSenderImpl;
import net.octopvp.commander.bukkit.impl.BukkitCommandWrapper;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.lang.LocalizedCommandException;
import net.octopvp.commander.lang.ResponseHandler;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class BukkitPlatform implements CommanderPlatform {
    private final Plugin plugin;
    private Commander commander;
    private CommandMap commandMap;

    public BukkitPlatform(Plugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (CommandMap) field.get(manager);
            } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleMessage(String message, CoreCommandSender sender) {
        BukkitCommandSender s = (BukkitCommandSender) sender;
        s.sendMessage(message);
    }

    @Override
    public void handleError(String error, CoreCommandSender sender) {
        BukkitCommandSender s = (BukkitCommandSender) sender;
        s.sendMessage(ChatColor.RED + error);
    }

    @Override
    public void handleCommandException(CommandContext ctx, CommandException e) {
        handleError(e.getMessage(), ctx.getCommandSender());
    }

    @Override
    public void handleCommandException(CommandInfo info, CoreCommandSender sender, CommandException e) {
        if (e instanceof LocalizedCommandException) {
            LocalizedCommandException lce = (LocalizedCommandException) e;
            ResponseHandler handler = lce.getResponseHandler();
            if (handler == null) {
                Bukkit.getLogger().severe("Could not find a instance of ResponseHandler to handle command exception: " + e.getClass().getName());
                return;
            }
            sender.sendMessage(ChatColor.RED + handler.getMessage(lce, lce.getPlaceholders()));
        } else sender.sendMessage(ChatColor.RED + e.getMessage());
    }

    @Override
    public void registerCommand(CommandInfo command) {
        if (commandMap.getCommand(command.getName()) == null) {
            Command cmd = new BukkitCommandWrapper(command);
            commandMap.register(plugin.getName(), cmd);
        }
    }

    @Override
    public boolean isSenderParameter(ParameterInfo parameterInfo) {
        return parameterInfo.getParameter().isAnnotationPresent(Sender.class) || parameterInfo.getParameter().getName().equalsIgnoreCase("sender") || parameterInfo.getParameter().getType().equals(CoreCommandSender.class)
                || parameterInfo.getParameter().getType().equals(BukkitCommandSender.class) || parameterInfo.getParameter().getType().equals(CommandSender.class) || parameterInfo.getParameter().getType().equals(BukkitCommandSenderImpl.class);
    }

    @Override
    public HelpService getHelpService() {
        return BukkitCommander.HELP_SERVICE;
    }

    @Override
    public Collection<Class<?>> getClassesInPackage(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        for (URL url : ClasspathHelper.forClassLoader(ClasspathHelper.contextClassLoader(),
                ClasspathHelper.staticClassLoader(), plugin
                        .getClass().getClassLoader())) {
            Vfs.Dir dir = Vfs.fromURL(url);
            try {
                for (Vfs.File file : dir.getFiles()) {
                    String name = file.getRelativePath().replace("/", ".").replace(".class", "");
                    if (name.startsWith(packageName))
                        classes.add(Class.forName(name));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                dir.close();
            }
        }
        return ImmutableSet.copyOf(classes);
    }
}
