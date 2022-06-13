package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CooldownTest {
    private int runs = 0;

    @Test
    public void testCooldown() throws InterruptedException {
        Commander commander = new CommanderImpl(new TestPlatform())
                .init()
                .register(this);

        commander.executeCommand(new CommandSender(), "test", null);
        assertEquals(1, runs);
        commander.executeCommand(new CommandSender(), "test", null);
        assertEquals(1, runs);
        Thread.sleep(1500);
        commander.executeCommand(new CommandSender(), "test", null);
        assertEquals(2, runs);
    }
    @Command(name = "test")
    @Cooldown(1)
    public void testCommand() {
        runs++;
    }
}
