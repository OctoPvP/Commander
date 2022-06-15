package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import net.octopvp.commander.exception.TestException;
import org.junit.jupiter.api.Test;

public class LocaleTest {
    private Commander commander;

    @Test
    public void test() {
        commander = new CommanderImpl(new TestPlatform())
                .init()
                .register(this);
        commander.executeCommand(new CommandSender(), "test", null);
    }

    @Command(name = "test")
    @Cooldown(1)
    public void testCommand() {
        throw new TestException(commander.getResponseHandler());
    }
}
