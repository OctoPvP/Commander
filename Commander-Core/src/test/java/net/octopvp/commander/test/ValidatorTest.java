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
import net.octopvp.commander.annotation.DefaultNumber;
import net.octopvp.commander.annotation.Range;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.ValidateException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorTest {
    private Commander commander;
    private boolean success = true;

    private int i = 0;

    @Test
    public void validateTest() {
        commander = new CommanderImpl(new TestPlatform())
                .init()
                .register(this)
                .registerProvider(TestClass.class, new TestClassProvider())
                .registerValidator(TestClass.class, (value, parameter, context) -> {
                    if (value.getStr().equals("1337 h4x0r")) {
                        throw new ValidateException("It works lol");
                    }
                })
        ;

        commander.executeCommand(new CommandSender(), "test", new String[]{"1337 h4x0r"});
        assertTrue(success);

        commander.executeCommand(new CommandSender(), "t", new String[]{"1"}); //TODO - chop off trailing decimal places in error
        assertTrue(success);

        commander.executeCommand(new CommandSender(), "testdefault", new String[]{});
        assertEquals(20, i);
    }

    @Command(name = "test")
    public void test(TestClass clazz) {
        success = false;
    }


    @Command(name = "t")
    public void t(@Range(min = 2) int i) {
        success = false;
    }

    @Command(name = "testdefault")
    public void testDefault(@DefaultNumber(20) int i) {
        this.i = i;
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
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return null;
        }
    }

}
