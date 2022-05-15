package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Command(name = "test", description = "test")
public class SubCommandTest {
    private Commander commander;
    private boolean passed = false, passed2 = false;

    @Test
    public void test() {
        commander = new CommanderImpl(new TestPlatform())
                //.init()
                .registerProvider(String.class, new StringProvider())
                .registerProvider(TestClass.class, new TestProvider())
                .registerProvider(TestClassTwo.class, new TestProvider2())
                .registerProvider(TestClassThree.class, new TestProvider3())
                .register(this);

        commander.executeCommand(new CommandSender(), "test", new String[]{"sub", "hello_world!"});
        assertTrue(passed);

        commander.executeCommand(new CommandSender(), "test", new String[]{"sub2", "hello_world!"});
        assertTrue(passed2);

        List<String> completions = commander.getSuggestions(new CommandSender(), "test completer");
        assertNotNull(completions);
        assertTrue(completions.contains("Hello") && completions.contains("World"));

        List<String> completions2 = commander.getSuggestions(new CommandSender(), "test completer abcd");
        assertNotNull(completions2);
        assertTrue(completions2.contains("Yes") && completions2.contains("ABCDEFG"));

        List<String> completions3 = commander.getSuggestions(new CommandSender(), "test completer abcd def");
        assertNotNull(completions3);
        assertTrue(completions3.contains("It Works!") && completions3.contains(":)"));
    }


    @Command(name = "sub", description = "Sub command!")
    public void testSub(String msg) {
        System.out.println("Sub command! " + msg);
        passed = msg.equals("hello_world!");
    }

    @Command(name = "sub2", description = "Sub command 2!")
    public void testSub2(String msg) {
        System.out.println("Sub command 2! " + msg);
        passed2 = msg.equals("hello_world!");
    }

    @Command(name = "completer")
    public void testComplete(TestClass testClass, TestClassTwo testClassTwo, TestClassThree three) {

    }

    private class StringProvider implements Provider<String> {

        @Override
        public String provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return args.pop();
        }

        @Override
        public List<String> provideSuggestions(String input) {
            return null;
        }
    }

    private static class TestProvider implements Provider<TestClass> {

        @Override
        public TestClass provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClass();
        }

        @Override
        public List<String> provideSuggestions(String input) {
            return Arrays.asList("Hello", "World");
        }
    }

    private static class TestProvider2 implements Provider<TestClassTwo> {

        @Override
        public TestClassTwo provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClassTwo();
        }

        @Override
        public List<String> provideSuggestions(String input) {
            return Arrays.asList("Yes", "ABCDEFG");
        }
    }

    private static class TestProvider3 implements Provider<TestClassThree> {

        @Override
        public TestClassThree provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClassThree();
        }

        @Override
        public List<String> provideSuggestions(String input) {
            return Arrays.asList("It Works!", ":)");
        }
    }

    private static class TestClass {
        public String getString() {
            return "Hello World!";
        }
    }

    private static class TestClassTwo {

    }

    private static class TestClassThree {

    }
}
