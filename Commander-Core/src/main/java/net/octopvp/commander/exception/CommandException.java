package net.octopvp.commander.exception;

public class CommandException extends RuntimeException{
    public CommandException(String message){
        super(message);
    }
    public CommandException(String message, Throwable cause){
        super(message, cause);
    }
    public CommandException(Throwable cause){
        super(cause);
    }
    public CommandException() {}
}
