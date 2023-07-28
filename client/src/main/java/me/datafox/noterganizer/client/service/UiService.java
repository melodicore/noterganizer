package me.datafox.noterganizer.client.service;

import javafx.stage.Stage;

/**
 * The UI service handles the JavaFx stage and loading different scenes to it
 * using the {@link me.datafox.noterganizer.client.factory.UiFactory}.
 *
 * @author datafox
 */
public interface UiService {
    /**
     * @param stage JavaFx stage to be used
     */
    void setStage(Stage stage);

    /**
     * @return current JavaFx stage
     */
    Stage getStage();

    /**
     * @param name name of the view to be loaded
     */
    void setScene(String name);
}
