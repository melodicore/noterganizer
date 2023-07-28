package me.datafox.noterganizer.client.model;

import javafx.scene.Parent;
import lombok.Builder;
import lombok.Data;

/**
 * Data for defining a JavaFx view.
 *
 * @author datafox
 */
@Data
@Builder
public class UiData<T> {
    private Parent ui;

    private T controller;
}
