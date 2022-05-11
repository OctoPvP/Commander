package net.octopvp.commander.test;

import net.octopvp.commander.sender.CoreCommandSender;

public class CommandSender implements CoreCommandSender {
    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    public String getName() {
        return "test";
    }
}
