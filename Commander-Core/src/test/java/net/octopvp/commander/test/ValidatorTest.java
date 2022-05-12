package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import org.junit.jupiter.api.Test;

public class ValidatorTest {
    private Commander commander;
    @Test
    public void validateTest() {
        commander = new CommanderImpl(new TestPlatform())
                .init()
                .register(this) //unfinished
                ;
    }

}
