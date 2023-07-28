package me.datafox.noterganizer.client.model;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;
import me.datafox.noterganizer.client.event.EventSubscription;

/**
 * Contains all non-persisted application data that is accessed by multiple components
 * during runtime.
 *
 * @author datafox
 */
public class Context {
    /**
     * Server address.
     */
    @Getter
    @Setter
    private String address;

    /**
     * Currently logged-in user.
     */
    @Getter
    @Setter
    private User user;

    /**
     * Currently selected space.
     */
    private final ObjectProperty<Space> space;

    /**
     * Currently selected note.
     */
    private final ReadOnlyObjectWrapper<Note> note;

    /**
     * All notes that are present within current space.
     */
    @Getter
    private final ObservableMap<String, Note> notes;

    /**
     * Current TreeView in the main view.
     */
    private final ObjectProperty<TreeView<Note>> treeView;

    /**
     * Simple event system, should be called every time the note tree is changed.
     */
    @Getter
    private final EventSubscription noteTreeChangeSubscription;

    public Context() {
        address = "";
        user = null;
        space = new SimpleObjectProperty<>();
        note = new ReadOnlyObjectWrapper<>();
        notes = FXCollections.observableHashMap();
        treeView = new SimpleObjectProperty<>();
        noteTreeChangeSubscription = new EventSubscription();

        //Initialise note listener so that it is the same instance every time
        ChangeListener<TreeItem<Note>> noteListener = this::noteChanged;

        //Add listener to the tree view property that attaches and detaches noteListener from tree views
        treeView.addListener((observable, oldValue, newValue) -> treeChanged(oldValue, newValue, noteListener));
    }

    public Space getSpace() {
        return space.get();
    }

    public void setSpace(Space space) {
        this.space.set(space);
    }

    public ObjectProperty<Space> spaceProperty() {
        return space;
    }

    public Note getNote() {
        return note.get();
    }

    public ReadOnlyObjectProperty<Note> noteProperty() {
        return note.getReadOnlyProperty();
    }

    public TreeView<Note> getTreeView() {
        return treeView.get();
    }

    public void setTreeView(TreeView<Note> treeView) {
        this.treeView.set(treeView);
    }

    /**
     * Clear everything to default values.
     */
    public void clear() {
        address = "";
        user = null;
        space.set(null);
        note.set(null);
        notes.clear();
        treeView.set(null);
    }

    /**
     * Change the read-only note property whenever a new note in the tree view is selected.
     */
    private void noteChanged(ObservableValue<? extends TreeItem<Note>> ignored, TreeItem<Note> ignored1, TreeItem<Note> newValue) {
        if(newValue == null) {
            note.set(null);
        } else {
            note.set(newValue.getValue());
        }
    }

    /**
     * Attach and detach the note listener whenever the tree view property is changed.
     */
    private void treeChanged(TreeView<Note> oldValue, TreeView<Note> newValue, ChangeListener<TreeItem<Note>> noteListener) {
        if(oldValue != null) {
            oldValue.getSelectionModel().selectedItemProperty().removeListener(noteListener);
            note.set(null);
        }
        if(newValue != null) {
            newValue.getSelectionModel().selectedItemProperty().addListener(noteListener);
        }
    }
}
