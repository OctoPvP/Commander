package net.octopvp.commander.sender;

import net.octopvp.commander.exception.CommandParseException;

public interface CoreCommandSender {
    boolean hasPermission(String permission);
}
