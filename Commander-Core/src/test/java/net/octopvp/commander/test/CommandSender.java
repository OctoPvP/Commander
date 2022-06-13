package net.octopvp.commander.test;

import net.octopvp.commander.sender.CoreCommandSender;
import java.util.UUID;

public class CommandSender implements CoreCommandSender {
    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public UUID getIdentifier() {
        return new UUID(0,0);
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    public String getName() {
        return "test";
    }
}
