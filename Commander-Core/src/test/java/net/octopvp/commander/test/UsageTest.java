package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Name;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Required;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsageTest {
    @Test
    public void testUsage() {
        Commander commander = new CommanderImpl(new TestPlatform())
                .init()
                .register(this);
        assertEquals("<arg1> [arg2]", commander.getCommandMap().get("test").getUsage());
    }
    @Command(name = "test")
    private void test(@Required @Name("arg1") String arg1, @Optional @Name("arg2") String arg2) {

    }
}
