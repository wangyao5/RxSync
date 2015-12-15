package com.rxsync.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;


@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
@Documented
public @interface Defer {
    String value() default "";
}
