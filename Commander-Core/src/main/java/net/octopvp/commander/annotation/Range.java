package net.octopvp.commander.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {
    double min() default Double.MIN_VALUE;
    double max() default Double.MAX_VALUE;
}
