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

import lombok.RequiredArgsConstructor;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.lang.LocalizedCommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class BukkitCommandSenderImpl implements BukkitCommandSender {
    private final CommandSender commandSender;

    @Override
    public CommandSender getSender() {
        return commandSender;
    }

    @Override
    public boolean isPlayer() {
        return commandSender instanceof Player;
    }

    @Override
    public boolean isConsole() {
        return commandSender instanceof ConsoleCommandSender;
    }

    @Override
    public boolean isOp() {
        return commandSender.isOp();
    }

    @Override
    public Player getPlayer() {
        if (!(isPlayer())) throw new LocalizedCommandException("must-be.player");
        return (Player) commandSender;
    }

    @Override
    public UUID getUUID() {
        return isPlayer() ? getPlayer().getUniqueId() : new UUID(0, 0);
    }

    @Override
    public String getName() {
        return commandSender.getName();
    }

    @Override
    public void sendMessage(String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }

    @Override
    public UUID getIdentifier() {
        return getUUID();
    }
}
