package net.octopvp.commander.command;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.annotation.Completer;

import java.lang.reflect.Method;

@Getter
@Setter
public class CompleterInfo {
    private CommandInfo commandInfo;
    private Object instance;
    private Method method;
    private Completer completer;

    private CommandInfo parent;

    private boolean subCommand = false;

    public CompleterInfo(CommandInfo commandInfo, Object instance, Method method, Completer completer) {
        this.commandInfo = commandInfo;
        this.instance = instance;
        this.method = method;
        this.completer = completer;
    }
    public CompleterInfo(CommandInfo commandInfo, Object instance, Method method, Completer completer, CommandInfo parent) {
        this.commandInfo = commandInfo;
        this.instance = instance;
        this.method = method;
        this.completer = completer;
        this.subCommand = true;
        this.parent = parent;
    }
}
