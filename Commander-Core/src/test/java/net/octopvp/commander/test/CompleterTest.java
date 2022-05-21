package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompleterTest {
    private Commander commander;

    @Test
    public void testCompletions() {
        commander = new CommanderImpl(new TestPlatform())
                .init()
                .register(this)
                .registerProvider(TestClass.class,new TestProvider())
                .registerProvider(TestClassTwo.class,new TestProvider2())
                .registerProvider(TestClassThree.class, new TestProvider3())
        ;
        List<String> completions = commander.getSuggestions(new CommandSender(), "test");
        assertNotNull(completions);
        assertFalse(completions.isEmpty());
        assertTrue(completions.contains("Hello") && completions.contains("World"));
        System.out.println("First passed.");
        List<String> completions2 = commander.getSuggestions(new CommandSender(), "test abcd");
        assertNotNull(completions2);
        assertFalse(completions2.isEmpty());
        assertTrue(completions2.contains("Yes") && completions2.contains("ABCDEFG"));
        System.out.println("Second passed.");
        List<String> completions3 = commander.getSuggestions(new CommandSender(), "test abcd def");
        assertNotNull(completions3);
        assertFalse(completions3.isEmpty());
        assertTrue(completions3.contains("It Works!") && completions3.contains(":)"));
        System.out.println("Third passed.");
    }
    @Command(name = "test")
    public void test(TestClass testClass, TestClassTwo testClassTwo, TestClassThree three) {
    }

    private static class TestProvider implements Provider<TestClass> {

        @Override
        public TestClass provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClass();
        }

        @Override
        public List<String> provideSuggestions(String input, CoreCommandSender sender) {
            return Arrays.asList("Hello", "World");
        }
    }
    private static class TestProvider2 implements Provider<TestClassTwo> {

        @Override
        public TestClassTwo provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClassTwo();
        }

        @Override
        public List<String> provideSuggestions(String input, CoreCommandSender sender) {
            return Arrays.asList("Yes", "ABCDEFG");
        }
    }
    private static class TestProvider3 implements Provider<TestClassThree> {

        @Override
        public TestClassThree provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClassThree();
        }

        @Override
        public List<String> provideSuggestions(String input, CoreCommandSender sender) {
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
