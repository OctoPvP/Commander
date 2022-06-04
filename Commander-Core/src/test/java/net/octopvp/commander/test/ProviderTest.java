package net.octopvp.commander.test;

import net.octopvp.commander.Commander;
import net.octopvp.commander.CommanderImpl;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProviderTest {
    private Commander commander;
    private String name;
    private int i;
    @Test
    public void testProviders() {
        commander = new CommanderImpl(new TestPlatform())
                .registerProvider(CommandSender.class, new TestProvider())
                .init();

        commander.register(this);

        commander.executeCommand(new CommandSender(), "test", new String[]{"1337"});

        assertEquals("test", name);
        assertEquals(1337, i);
    }

    @Command(name = "test")
    public void test(@Sender CommandSender sender, int i) {
        name = sender.getName();
        this.i = i;
    }
    private class TestProvider implements Provider<CommandSender> {

        @Override
        public CommandSender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
            return (CommandSender) context.getCommandSender();
        }

        @Override
        public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
            return null;
        }
    }
}
