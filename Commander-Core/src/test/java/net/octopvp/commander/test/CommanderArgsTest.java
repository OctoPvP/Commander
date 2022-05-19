package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Flag;
import net.octopvp.commander.annotation.Required;
import net.octopvp.commander.annotation.Switch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommanderArgsTest {
    private Commander commander;
    private String arg;

    private String[] args;
    @Test
    public void testArgs() {
        commander = new CommanderImpl(new TestPlatform()).init();

        commander.register(this);

        commander.executeCommand(new CommandSender(), "test", new String[]{"arg", "arg2"});
        assertEquals("arg", arg);
    }

    @Test
    public void testSwitchesAndFlags() {
        commander = new CommanderImpl(new TestPlatform()).init();

        commander.register(this);
        commander.executeCommand(new CommandSender(),"testswitchesandflags", new String[]{"arg", "-s", "-f", "flag"});
        assertEquals("argtrueflag", arg);
    }

    @Test
    public void testSwitches() {
        commander = new CommanderImpl(new TestPlatform()).init();

        commander.register(this);

        commander.executeCommand(new CommandSender(),"testswitches", new String[]{"-s"});
        assertEquals("true", arg);
    }

    @Test
    public void testFlags() {
        commander = new CommanderImpl(new TestPlatform()).init();

        commander.register(this);

        commander.executeCommand(new CommandSender(),"testflags", new String[]{"-f", "flag"});
        assertEquals("flag", arg);
    }

    @Test
    public void testAllStrings() {
        commander = new CommanderImpl(new TestPlatform()).init();

        commander.register(this);

        commander.executeCommand(new CommandSender(), "testmore", new String[]{"arg1", "\"arg2", "arg3", "arg4\"", "arg5"});
        for (int i = 0; i < args.length; i++) {
            if (i == 0) {
                assertEquals("arg1", args[i]);
            }else if (i == 1) {
                assertEquals("arg2 arg3 arg4", args[i]);
            }else if (i == 2) {
                assertEquals("arg5", args[i]);
            }else throw new RuntimeException("Too many args!");
        }
    }

    @Command(name = "testflags")
    public void testFlags(@Flag("f") String flag) {
        this.arg = flag;
    }

    @Command(name = "testswitches")
    public void testSwitches(@Switch("s") boolean sw) {
        this.arg = sw + "";
    }

    @Command(name = "testswitchesandflags")
    public void testSwitchesAndFlags(String arg, @Switch("s") boolean sw, @Flag("f") String flag) {
        this.arg = arg + sw + flag;
    }

    @Command(name = "test")
    public void test(@Required String arg) {
        this.arg = arg;
    }

    @Command(name = "testmore")
    public void testMore(@Required String[] args) {
        this.args = args;
    }
}
