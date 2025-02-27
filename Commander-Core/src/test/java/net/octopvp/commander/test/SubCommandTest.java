/*
 * Copyright (c) Evan Yu 2024.
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.config.CommanderConfig;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Command(name = "test", description = "test")
public class SubCommandTest {
    private Commander commander;
    private boolean passed = false, passed2 = false, passed3 = false;

    @Test
    public void test() {
        commander = new CommanderImpl(new TestPlatform())
                .setConfig(new CommanderConfig.Builder()
                        .setFilterSuggestions(false)
                        .build())
                .registerProvider(String.class, new StringProvider())
                .registerProvider(TestClass.class, new TestProvider())
                .registerProvider(TestClassTwo.class, new TestProvider2())
                .registerProvider(TestClassThree.class, new TestProvider3())
                .register(this);

        commander.executeCommand(new CommandSender(), "test", new String[]{"sub", "hello_world!"});
        assertTrue(passed);

        commander.executeCommand(new CommandSender(), "test", new String[]{"sub2", "hello_world!"});
        assertTrue(passed2);

        commander.executeCommand(new CommandSender(), "test", new String[]{"hello_world!"});
        assertTrue(passed3);

        List<String> completions = commander.getSuggestions(new CommandSender(), "test completer ", null);
        assertNotNull(completions);
        assertTrue(completions.contains("Hello") && completions.contains("World"));

        List<String> completions2 = commander.getSuggestions(new CommandSender(), "test completer abcd ", null);
        assertNotNull(completions2);
        assertTrue(completions2.contains("Yes") && completions2.contains("ABCDEFG"));

        List<String> completions3 = commander.getSuggestions(new CommandSender(), "test completer abcd def ", null);
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

    @Command(name = "", description = "Testing root level")
    public void testRoot() {
        System.out.println("Root command! ");
        passed3 = true;
    }

    @Command(name = "completer")
    public void testComplete(@Sender CommandSender sender, TestClass testClass, TestClassTwo testClassTwo, TestClassThree three) {

    }

    private class StringProvider implements Provider<String> {

        @Override
        public String provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return args.pop();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return null;
        }
    }

    private static class TestProvider implements Provider<TestClass> {

        @Override
        public TestClass provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClass();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return Arrays.asList("Hello", "World");
        }
    }

    private static class TestProvider2 implements Provider<TestClassTwo> {

        @Override
        public TestClassTwo provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClassTwo();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return Arrays.asList("Yes", "ABCDEFG");
        }
    }

    private static class TestProvider3 implements Provider<TestClassThree> {

        @Override
        public TestClassThree provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return new TestClassThree();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
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
