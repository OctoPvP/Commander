package net.octopvp.commander.bukkit.impl;

import lombok.RequiredArgsConstructor;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.exception.CommandException;
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
    public Player getPlayer() {
        if (!(isPlayer())) throw new CommandException("You must be a player to do this!");
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
    public boolean isOperator() {
        return commandSender.isOp();
    }

    @Override
    public UUID getIdentifier() {
        return getUUID();
    }
}
