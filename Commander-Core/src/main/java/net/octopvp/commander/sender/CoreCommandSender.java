package net.octopvp.commander.sender;

import net.octopvp.commander.exception.CommandParseException;

import java.util.UUID;

public interface CoreCommandSender {
    boolean hasPermission(String permission);

    UUID getIdentifier();

    void sendMessage(String message);
}
