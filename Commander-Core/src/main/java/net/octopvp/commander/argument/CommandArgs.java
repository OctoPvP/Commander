package net.octopvp.commander.argument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.commander.Commander;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class CommandArgs {
    private final Commander commander;

    private final String[] args;

    private final Map<String,Boolean> switches;

    private final Map<String,String> flags;

    private final List<String> argsList;

    private Deque<String> argsDeque;

    public Deque<String> getArgs() {
        if (argsDeque == null) {
            argsDeque = new ArrayDeque<>(argsList);
        }

        return argsDeque;
    }
}
