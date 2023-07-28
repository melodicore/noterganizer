package me.datafox.noterganizer.client.injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A constructor annotation that tells the {@link Injector} to inject dependencies
 * when the annotated class is instantiated, either for classes annotated with
 * {@link Component} or any arbitrary class instantiated by using
 * {@link Injector#newInstance(Class)}. A class must have exactly one constructor
 * annotated with {@link Inject}, otherwise an error is thrown during instantiation.
 *
 * @author datafox
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
