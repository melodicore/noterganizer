package me.datafox.noterganizer.client;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Constant fields for the client.
 *
 * @author datafox
 */
public class ClientConstants {
    public static final DataFormat SERIALIZED_FORMAT = new DataFormat("application/x-java-serialized-object");

    public static final RowConstraints ALWAYS_ROW = new RowConstraints();
    public static final RowConstraints NEVER_ROW = new RowConstraints();
    public static final ColumnConstraints ALWAYS_COLUMN = new ColumnConstraints();
    public static final ColumnConstraints NEVER_COLUMN = new ColumnConstraints();

    public static final double POPUP_WIDTH = 300;
    public static final double POPUP_HEIGHT = 180;

    static {
        ALWAYS_ROW.setVgrow(Priority.ALWAYS);
        ALWAYS_ROW.setValignment(VPos.TOP);
        NEVER_ROW.setVgrow(Priority.NEVER);
        ALWAYS_COLUMN.setHgrow(Priority.ALWAYS);
        ALWAYS_COLUMN.setHalignment(HPos.LEFT);
        NEVER_COLUMN.setHgrow(Priority.NEVER);
    }
}
