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
import net.octopvp.commander.annotation.Dependency;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DependencyTest {
    private Commander commander;
    private static TestStaticDependency staticDep = new TestStaticDependency();
    private String arg;
    private boolean passedDep1, passedDep2;
    private static String s;
    @Test
    public void testDependency() {
        commander = new CommanderImpl(new TestPlatform())
                .init()
                .registerDependency(TestStaticDependency.class, staticDep)
                .registerDependency(TestDependency.class, (Supplier<TestDependency>) TestDependency::new)
                .register(this);

        commander.executeCommand(new CommandSender(), "test", new String[]{"hello"});
        assertTrue(passedDep1);
        assertTrue(passedDep2);
        assertEquals("hello", arg);
    }

    @Command(name = "test")
    public void testDependencyCommand(@Dependency TestDependency dependency1, @Dependency TestStaticDependency dependency2, String arg) {
        this.arg = arg;
        if (dependency1 != null && dependency1.returnThing().equals(s))
            passedDep1 = true;
        if (dependency2 != null && dependency2.returnThing().equals("abcd"))
            passedDep2 = true;
    }

    private static class TestStaticDependency {
        public String returnThing() {
            return "abcd";
        }
    }
    private static class TestDependency {
        public String returnThing() {
            return s = System.currentTimeMillis() + "";
        }
    }
}
