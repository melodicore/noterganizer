package me.datafox.noterganizer.client.factory;

import javafx.stage.Stage;
import me.datafox.noterganizer.client.model.PopupData;

/**
 * A factory for building popup windows.
 *
 * @author datafox
 */
public interface PopupFactory {
    /**
     * @param data popup data
     * @return stage created from the popup data
     */
    Stage buildPopup(PopupData data);
}
