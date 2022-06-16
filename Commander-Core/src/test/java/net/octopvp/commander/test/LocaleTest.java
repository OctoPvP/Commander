package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.lang.LocalizedCommandException;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocaleTest {
    private Commander commander;

    private boolean passed = false;

    @Test
    public void test() {
        commander = new CommanderImpl(new TestPlatform() {
            @Override
            public void handleLocale(Exception e, Commander commander) {
                String s = commander.getResponseHandler().getMessage(e);
                System.out.println("Localized: " + s);
                if (s.equals("Hello World!")) {
                    passed = true;
                }
            }
        })
                .init()
                .register(this);
        commander.getResponseHandler().addBundle(ResourceBundle.getBundle("test", Locale.ENGLISH));
        commander.executeCommand(new CommandSender(), "test", null);
        assertTrue(passed);
    }

    @Command(name = "test")
    public void testCommand() {
        throw new TestException("message.test");
    }

    public class TestException extends LocalizedCommandException {
        public TestException(String key) {
            super(key);
        }
    }
}
