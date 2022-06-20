/*
 * Copyright (c) Badbird5907 2022.
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

import static org.junit.jupiter.api.Assertions.*;

public class CompleterTest {
    private Commander commander;

    @Test
    public void testCompletions() {
        commander = new CommanderImpl(new TestPlatform())
                .setConfig(new CommanderConfig.Builder()
                        .setFilterSuggestions(false)
                        .build())
                .init()
                .register(this)
                .registerProvider(TestClass.class,new TestProvider())
                .registerProvider(TestClassTwo.class,new TestProvider2())
                .registerProvider(TestClassThree.class, new TestProvider3())
        ;
        List<String> completions = commander.getSuggestions(new CommandSender(), "test ");
        assertNotNull(completions);
        assertFalse(completions.isEmpty());
        assertTrue(completions.contains("Hello") && completions.contains("World"));
        System.out.println("First passed.");
        List<String> completions2 = commander.getSuggestions(new CommandSender(), "test abcd ");
        assertNotNull(completions2);
        assertFalse(completions2.isEmpty());
        assertTrue(completions2.contains("Yes") && completions2.contains("ABCDEFG"));
        System.out.println("Second passed.");
        List<String> completions3 = commander.getSuggestions(new CommandSender(), "test abcd def ");
        assertNotNull(completions3);
        assertFalse(completions3.isEmpty());
        assertTrue(completions3.contains("It Works!") && completions3.contains(":)"));
        System.out.println("Third passed.");
    }
    @Command(name = "test")
    public void test(@Sender CommandSender sender, TestClass testClass, TestClassTwo testClassTwo, TestClassThree three) {
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
