package me.datafox.noterganizer.client.injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A type annotation that tells the {@link Injector} to handle it as a
 * singleton bean. A class with this annotation must either have exactly
 * one constructor annotated with {@link Inject} or a default constructor.
 *
 * @author datafox
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
}
