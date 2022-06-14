package net.octopvp.commander.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Duration {

    /**
     * Should we calculate to the future or the past?
     * @return
     */
    boolean future() default true;

    /**
     * Weather or not to allow permanent durations.
     *
     * If true, permantent durations will return -1.
     * @return
     */
    boolean allowPermanent() default false;

    /**
     * Default duration, parsed just like how it would be if the user did specify a duration.
     * @return
     */
    String defaultValue() default "";

    //TODO maybe add min/max values?
}
