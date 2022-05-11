package net.octopvp.commander.platform;

import net.octopvp.commander.sender.CoreCommandSender;

public interface CommanderPlatform {
    void handleMessage(String message, CoreCommandSender sender);

    void handleError(String error, CoreCommandSender sender);

    default boolean hasPermission(CoreCommandSender sender, String permission){
        return sender.hasPermission(permission);
    }
}
