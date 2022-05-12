package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CooldownTest {
    private int runs = 0;
    private Commander commander;
    @Test
    public void testCooldown() {
        commander = new CommanderImpl(new TestPlatform())
                .init()
                .register(this);

        commander.executeCommand(new CommandSender(), "test", null);
        assertEquals(1, runs);
        commander.executeCommand(new CommandSender(), "test", null);
        try {
        } catch (Exception ignored) {}
        assertEquals(1, runs);
    }
    @Command(name = "test")
    @Cooldown(10)
    public void testCommand() {
        runs++;
    }
}
