package net.octopvp.commander.annotation;

public @interface GetArgumentFor {
    /**
     * The index of the processed user-entered argument to get. Starting at 0
     * @return
     */
    int value();
}
