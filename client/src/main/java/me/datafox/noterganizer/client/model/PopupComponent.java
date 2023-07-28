package me.datafox.noterganizer.client.model;

import javafx.scene.Node;
import lombok.*;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A popup component contains a JavaFx node, and optionally a string converter
 * function and/or an initializer function to run additional operations on the
 * node inline within the builder.
 *
 * @author datafox
 */
@Data
@Builder
public class PopupComponent<T extends Node> {
    private T node;

    private Function<T,String> converter;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Consumer<T> initializer;

    PopupComponent(T node, Function<T,String> converter, Consumer<T> initializer) {
        this.node = node;
        this.converter = converter;
        this.initializer = initializer;

        if(initializer != null) initializer.accept(node);
    }

    public String convert() {
        return converter.apply(node);
    }
}
