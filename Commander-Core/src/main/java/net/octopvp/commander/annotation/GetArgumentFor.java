package net.octopvp.commander.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface GetArgumentFor {
    /**
     * The index of the processed user-entered argument to get. Starting at 0
     *
     * @return
     */
    int value();
}
