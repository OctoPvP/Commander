package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.validator.Validator;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorTest {
    private Commander commander;
    private boolean success = true;
    @Test
    public void validateTest() {
        commander = new CommanderImpl(new TestPlatform())
                .init()
                .register(this)
                .registerProvider(TestClass.class, new TestClassProvider())
                .registerValidator(TestClass.class, (value, parameter, context) -> {
                    if (value.getStr().equals("1337 h4x0r")) {
                        throw new CommandException("It works lol");
                    }
                })
                ;

        commander.executeCommand(new CommandSender(), "test", new String[]{"1337 h4x0r"});
        assertTrue(success);
    }

    @Command(name = "test")
    public void test(TestClass clazz) {
        success = false;
    }


    private static class TestClass {
        public String getStr() {
            return "1337 h4x0r";
        }
    }

    private static class TestClassProvider implements Provider<TestClass> {

        @Override
        public TestClass provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClass();
        }

        @Override
        public List<String> provideSuggestions(String input) {
            return null;
        }
    }

}
