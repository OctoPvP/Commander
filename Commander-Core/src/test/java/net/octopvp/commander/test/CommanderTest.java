package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommanderTest {
    private Commander commander;
    @Test
    public void test() {
        commander = new CommanderImpl(new TestPlatform()).init();

        commander.register(this);

        commander.executeCommand(new CommandSender(),"test", new String[]{});
        assertTrue(testPassed);
    }

    private boolean testPassed = false;
    @Command(name = "test")
    public void command() {
        testPassed = true;
    }
}
