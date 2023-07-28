package me.datafox.noterganizer.client.model;

import javafx.scene.control.Button;
import lombok.Builder;
import lombok.Data;

/**
 * Data for a popup button.
 *
 * @author datafox
 */
@Data
@Builder
public class PopupButton {
    private Button button;

    @Builder.Default
    private boolean sendCallback = false;
}
