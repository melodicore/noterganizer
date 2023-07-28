package me.datafox.noterganizer.client.factory;

import javafx.scene.Parent;
import me.datafox.noterganizer.client.model.UiData;

/**
 * A factory for creating JavaFx views.
 *
 * @author datafox
 */
public interface UiFactory {
    /**
     * @param name name of the view
     * @return the controller and the root {@link Parent} of the view
     */
    <T> UiData<T> buildUi(String name);
}
