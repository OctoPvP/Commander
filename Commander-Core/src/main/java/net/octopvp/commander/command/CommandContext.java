package net.octopvp.commander.command;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.commander.argument.CommandArgs;
import net.octopvp.commander.sender.CoreCommandSender;

@Getter
@Setter
public class CommandContext {
    private CommandInfo commandInfo;
    private String label;
    private String[] originalArgs;
    private CoreCommandSender commandSender;
    private String permission;
    private CommandArgs args;

    public CommandContext(CommandInfo commandInfo, String label, String[] originalArgs, CoreCommandSender commandSender, CommandArgs commandArgs) {
        this.commandInfo = commandInfo;
        this.label = label;
        this.originalArgs = originalArgs;
        this.commandSender = commandSender;
        this.permission = commandInfo.getPermission();
        this.args = commandArgs;
    }
}
