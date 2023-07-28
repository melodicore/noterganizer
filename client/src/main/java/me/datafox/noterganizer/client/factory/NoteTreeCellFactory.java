package me.datafox.noterganizer.client.factory;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import me.datafox.noterganizer.client.model.Note;

/**
 * A factory for creating {@link TreeCell}s to be used within the main
 * {@link TreeView}. Should handle context menu creation and drag and drop
 * reordering functionality.
 *
 * @author datafox
 */
public interface NoteTreeCellFactory {
    TreeCell<Note> build(TreeView<Note> treeView);
}
