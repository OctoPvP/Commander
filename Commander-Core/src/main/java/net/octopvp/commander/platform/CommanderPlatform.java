package net.octopvp.commander.platform;

import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.help.HelpService;
import net.octopvp.commander.sender.CoreCommandSender;
import org.reflections.Reflections;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CommanderPlatform {
    void handleMessage(String message, CoreCommandSender sender);

    void handleError(String error, CoreCommandSender sender);

    void handleCommandException(CommandContext ctx, CommandException e);

    void handleCommandException(CommandInfo info, CoreCommandSender sender, CommandException e);

    default boolean hasPermission(CoreCommandSender sender, String permission){
        return sender.hasPermission(permission);
    }

    void registerCommand(CommandInfo command);

    default boolean isSenderParameter(ParameterInfo parameterInfo) {
        return parameterInfo.getParameter().isAnnotationPresent(Sender.class) ||
                parameterInfo.getParameter().getName().equalsIgnoreCase("sender") ||
                parameterInfo.getParameter().getType().equals(CoreCommandSender.class);
    }

    default String getPrefix(){
        return "/";
    }

    HelpService getHelpService();

    default Collection<Class<?>> getClassesInPackage(String packageName){
        return getReflections(packageName).getSubTypesOf(Object.class);
    }

    default Reflections getReflections(String packageName){
        return new Reflections(packageName);
    }
}
