package net.octopvp.commander.argument;

import lombok.Getter;
import net.octopvp.commander.Commander;

import java.util.*;

@Getter
public class CommandArgs {
    private final Commander commander;

    private final String[] args;

    private final Map<String,Boolean> switches;

    private final Map<String,String> flags;

    private final List<String> argsList;

    private final List<String> preservedArgs;

    private Deque<String> argsDeque;

    public CommandArgs(Commander commander, String[] args, Map<String, Boolean> switches, Map<String, String> flags, List<String> argsList) {
        this.commander = commander;
        this.args = args;
        this.switches = switches;
        this.flags = flags;
        this.argsList = argsList;
        this.preservedArgs = Collections.unmodifiableList(new ArrayList<>(argsList));
    }

    public Deque<String> getArgs() {
        if (argsDeque == null) {
            argsDeque = new ArrayDeque<>(argsList);
        }
        return argsDeque;
    }
}
