package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Required;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequiredTest {
    private boolean passed;
    @Test
    public void testRequired() {
        Commander commander = new CommanderImpl(new TestPlatform()).init();

        commander.register(this);
        commander.executeCommand(new CommandSender(), "test", new String[]{"HelloWorld!"});
        assertTrue(passed);
    }

    @Command(name = "test")
    public void test(@Required String requiredString, @Optional String optionalString) {
        if (requiredString.equals("HelloWorld!") && (optionalString == null || optionalString.isEmpty()))
            passed = true;
    }
}
