package me.datafox.noterganizer.client.factory.impl;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import me.datafox.noterganizer.client.factory.NoteTreeCellFactory;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.Context;
import me.datafox.noterganizer.client.model.Note;
import me.datafox.noterganizer.client.service.NoteService;
import me.datafox.noterganizer.client.service.PopupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static me.datafox.noterganizer.client.ClientConstants.SERIALIZED_FORMAT;

/**
 * Implementation of {@link NoteTreeCellFactory}
 *
 * @author datafox
 */
@Component
public class NoteTreeCellFactoryImpl implements NoteTreeCellFactory {
    private final Context context;

    private final NoteService noteService;

    private final PopupService popupService;

    private final Map<TreeView<Note>, NoteTreeData> noteTreeData;

    @Inject
    public NoteTreeCellFactoryImpl(Context context,
                                   NoteService noteService,
                                   PopupService popupService) {
        this.context = context;
        this.noteService = noteService;
        this.popupService = popupService;

        noteTreeData = new HashMap<>();
    }

    @Override
    public TreeCell<Note> build(TreeView<Note> treeView) {
        if(!noteTreeData.containsKey(treeView)) noteTreeData.put(treeView, new NoteTreeData());

        return new NoteTreeCell(noteTreeData.get(treeView));
    }

    /**
     * Custom {@link TreeCell} implementation.
     */
    private class NoteTreeCell extends TreeCell<Note> {
        private final Logger logger;

        private final NoteTreeData data;

        public NoteTreeCell(NoteTreeData data) {
            super();

            logger = LogManager.getLogger(getClass());

            this.data = data;

            setContextMenu(buildContextMenu());

            setOnDragDetected(event -> dragDetected(event, this));
            setOnDragOver(event -> dragOver(event, this));
            setOnDragDropped(event -> dragDropped(event, this));
        }

        @Override
        protected void updateItem(Note item, boolean empty) {
            super.updateItem(item, empty);

            if(empty || item == null) {
                logger.debug("Updating empty item");

                textProperty().unbind();
                setText(null);
                setGraphic(null);
            } else {
                logger.debug("Updating item for note " + item.getLogName());

                textProperty().bind(noteTitleBinding(item));
            }
        }

        /**
         * Creates a context menu with required functionality.
         */
        private ContextMenu buildContextMenu() {
            ContextMenu menu = new ContextMenu();

            MenuItem rename = new MenuItem("Rename");
            MenuItem create = new MenuItem("Create child");
            MenuItem delete = new MenuItem("Delete");
            MenuItem copy = new MenuItem("Copy UUID");

            delete.textProperty().bind(deletePromptBinding());

            rename.setOnAction(this::rename);
            create.setOnAction(this::create);
            delete.setOnAction(this::delete);
            copy.setOnAction(this::copy);

            menu.getItems().setAll(rename, create, delete, copy);

            return menu;
        }

        /**
         * Start drag and drop action.
         */
        private void dragDetected(MouseEvent event, TreeCell<Note> cell) {
            data.draggedItem = cell.getTreeItem();

            if(data.draggedItem == null || data.draggedItem.getParent() == null) return;

            logger.debug("Dragging detected for " + cell.getTreeItem().getValue().getLogName());

            Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();

            content.put(SERIALIZED_FORMAT, cell.getItem().getUuid());

            dragboard.setContent(content);

            dragboard.setDragView(cell.snapshot(null, null));

            event.consume();
        }

        /**
         * Prepare a cell for dropping a dragged item.
         */
        private void dragOver(DragEvent event, TreeCell<Note> cell) {
            if(!event.getDragboard().hasContent(SERIALIZED_FORMAT)) return;

            TreeItem<Note> overItem = cell.getTreeItem();

            if(data.draggedItem == null || overItem == null || Objects.equals(data.draggedItem, overItem)) return;

            event.acceptTransferModes(TransferMode.MOVE);

            if(!Objects.equals(cell, data.overCell)) {
                if(data.overCell != null) data.overCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("drag-over"), false);
                data.overCell = cell;
                data.overCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("drag-over"), true);
            }
        }

        /**
         * Finish drag and drop action and change structure in memory.
         */
        private void dragDropped(DragEvent event, TreeCell<Note> cell) {
            Dragboard dragboard = event.getDragboard();

            if(!dragboard.hasContent(SERIALIZED_FORMAT)) return;

            TreeItem<Note> overItem = cell.getTreeItem();

            boolean success = noteService.moveNote(data.draggedItem.getValue(), overItem.getValue());

            if(data.overCell != null) data.overCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("drag-over"), false);

            event.setDropCompleted(success);
        }

        /**
         * @return a string binding that returns "Delete note" unless the associated {@link TreeItem}
         * is the root item, in which case it returns "Delete space".
         */
        private ObservableValue<String> deletePromptBinding() {
            return Bindings.createStringBinding(() -> {
                if(getTreeItem() == null) return "";
                return "Delete " + (getTreeItem().getParent() == null ? "space" : "note");
            }, treeItemProperty());
        }

        /**
         * @param note note
         * @return a string binding that returns the note's title
         */
        private ObservableValue<String> noteTitleBinding(Note note) {
            return Bindings.createStringBinding(note::getTitle,note.titleProperty());
        }

        /**
         * Show a popup for renaming a note.
         */
        private void rename(ActionEvent ignored) {
            popupService.showTextPopup("Rename note",
                    "Please enter a new title for your note:",
                    title -> noteService.renameNote(getItem(), title));
        }

        /**
         * Show a popup for creating a note.
         */
        private void create(ActionEvent ignored) {
            popupService.showTextPopup("Create note",
                    "Please enter a title for your new note:",
                    title -> noteService.createNote(getItem(), title));
        }

        /**
         * Show a popup for deleting a note. If it is the root note, delete the whole space instead.
         */
        private void delete(ActionEvent ignored) {
            if(getTreeItem().getParent() == null) {
                popupService.showConfirmPopup("Remove space",
                        "Are you sure you want to remove " + getText() + "?\n" +
                                "This action cannot be undone!",
                        () -> noteService.removeSpace(context.getSpace().getUuid()));
            } else {
                popupService.showCheckboxConfirmPopup("Remove note",
                        "Are you sure you want to remove " + getText() + "?\n" +
                                "This action cannot be undone!",
                        "Remove children",
                        removeChildren -> noteService.removeNote(getItem(), removeChildren));
            }
        }

        /**
         * Copy the note's uuid to the system clipboard.
         */
        private void copy(ActionEvent ignored) {
            Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, getItem().getUuid()));
        }
    }

    /**
     * {@link TreeView} -specific data for orchestrating drag and drop functionality.
     */
    private static class NoteTreeData {
        private TreeItem<Note> draggedItem;

        private TreeCell<Note> overCell;
    }
}
