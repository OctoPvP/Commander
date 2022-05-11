package net.octopvp.commander;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.platform.CommanderPlatform;
import net.octopvp.commander.sender.CoreCommandSender;

public class Test {
    private static Commander commander;

    public static void main(String[] args) {
        commander = new CommanderImpl(new CommanderPlatform() {
            @Override
            public void handleMessage(String message, CoreCommandSender sender) {
                System.out.println(message);
            }

            @Override
            public void handleError(String error, CoreCommandSender sender) {
                System.err.println(error);
            }
        });
        commander.init().register(new Test());

        try {
            commander.executeCommand(permission -> true,"test", new String[]{"123"});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Command(name = "test", aliases = {"t"}, description = "Test command")
    public void onCommand(@Optional String aaa) {
        System.out.println(aaa);
    }
}
