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
    private static final TestStaticDependency staticDep = new TestStaticDependency();
    private String arg;
    private boolean passedDep1, passedDep2;
    private static String s;
    @Test
    public void testDependency() {
        Commander commander = new CommanderImpl(new TestPlatform())
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
