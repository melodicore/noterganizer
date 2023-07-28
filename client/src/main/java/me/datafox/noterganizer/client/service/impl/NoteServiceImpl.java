package me.datafox.noterganizer.client.service.impl;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import me.datafox.noterganizer.api.dto.*;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.Context;
import me.datafox.noterganizer.client.model.Note;
import me.datafox.noterganizer.client.model.Space;
import me.datafox.noterganizer.client.model.SpaceHeader;
import me.datafox.noterganizer.client.service.MappingService;
import me.datafox.noterganizer.client.service.NoteService;
import me.datafox.noterganizer.client.service.PopupService;
import me.datafox.noterganizer.client.service.RestService;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Note service implementation.
 *
 * @author datafox
 */
@Component
public class NoteServiceImpl implements NoteService {
    private final Context context;

    private final Logger logger;

    private final MappingService mappingService;

    private final RestService restService;

    private final PopupService popupService;

    @Inject
    public NoteServiceImpl(Context context,
                           Logger logger,
                           MappingService mappingService,
                           RestService restService,
                           PopupService popupService) {
        this.context = context;
        this.logger = logger;
        this.mappingService = mappingService;
        this.restService = restService;
        this.popupService = popupService;

        logger.info("Initializing note service");

        context.spaceProperty().addListener(new ChangeListener<Space>() {
            @Override
            public void changed(ObservableValue<? extends Space> observable, Space oldValue, Space newValue) {
                context.getNotes().clear();
                if(newValue != null) context.getNotes().putAll(notesFromSpace(newValue));
                TreeView<Note> treeView = context.getTreeView();
                if(treeView != null) {
                    if(newValue != null) {
                        context.getTreeView().setRoot(buildSpace(newValue));
                    } else {
                        context.getTreeView().setRoot(null);
                    }
                }
                context.getNoteTreeChangeSubscription().call();
            }
        });

        context.noteProperty().addListener(new ChangeListener<Note>() {
            @Override
            public void changed(ObservableValue<? extends Note> observable, Note oldValue, Note newValue) {
                if(oldValue != null && !oldValue.isRemoved()) saveNote(oldValue, false);
            }
        });
    }

    @Override
    public void createSpace(String title) {
        logger.info("Attempting to create space " + title);

        Optional<String> optional = restService.createSpace(SpaceCreateDto.of(title));

        if(optional.isEmpty()) return;

        context.setUser(context.getUser().toBuilder().space(SpaceHeader.builder()
                .uuid(optional.get())
                .name(title)
                .build()).build());

        loadSpace(optional.get());
    }

    @Override
    public void loadSpace(String uuid) {
        logger.info("Attempting to fetch space with uuid " + uuid);

        Optional<SpaceDto> optional = restService.getSpace(uuid);

        if(optional.isEmpty()) return;

        context.setSpace(mappingService.mapToSpace(optional.get()));
    }

    @Override
    public void removeSpace(String uuid) {
        logger.info("Attempting to remove space with uuid " + uuid);

        Optional<String> optional = restService.removeSpace(uuid);

        if(optional.isEmpty()) return;

        context.setUser(context.getUser()
                .toBuilder()
                .clearSpaces()
                .spaces(context.getUser()
                        .getSpaces()
                        .stream()
                        .filter(header -> !header.getUuid().equals(uuid))
                        .toList())
                .build());

        context.spaceProperty().set(null);
    }

    @Override
    public void createNote(Note parent, String title) {
        logger.info("Attempting to create note " + title + " as a child of " + parent.getLogName());

        Optional<String> optional = restService.createNote(NoteCreateDto.builder()
                .parent(mappingService.mapToNoteHeaderDto(parent))
                .title(title)
                .build());

        if(optional.isEmpty()) return;

        Note note = Note.builder()
                .uuid(optional.get())
                .title(title)
                .parent(parent)
                .build();

        parent.getChildren().add(note);

        context.getNotes().put(note.getUuid(), note);

        parent.getChildren().sort(Comparator.naturalOrder());

        TreeItem<Note> item = buildItem(note);

        TreeItem<Note> parentItem = parent.getItem();

        parentItem.getChildren().add(item);

        parentItem.getChildren().sort(Comparator.comparing(TreeItem::getValue));

        context.getNoteTreeChangeSubscription().call();
    }

    @Override
    public void openNote(String uuid) {
        Note note = context.getNotes().get(uuid);

        if(note == null) {
            popupService.showInfoPopup("Cannot open note", "The link you tried to open points to a note that does not exist");
            return;
        }

        context.getTreeView().getSelectionModel().clearSelection();
        context.getTreeView().getSelectionModel().select(note.getItem());
    }

    @Override
    public void renameNote(Note note, String title) {
        note.setTitle(title);

        if(note.getParent() != null) {
            note.getParent().getChildren().sort(Comparator.naturalOrder());

            note.getItem().getParent().getChildren().sort(Comparator.comparing(TreeItem::getValue));
        }

        saveNote(note, true);
    }

    @Override
    public void saveNote(Note note, boolean force) {
        if(!force && note.getContent().equals(note.getLastContent())) return;

        logger.info("Attempting to save note " + note.getLogName());

        Optional<String> optional = restService.changeNote(mappingService.mapToNoteChangeDto(note));

        if(optional.isEmpty()) {
            note.setContent(note.getLastContent());
            return;
        }

        note.setLastContent(note.getContent());
    }

    @Override
    public boolean moveNote(Note note, Note newParent) {
        logger.info("Attempting to move note " + note.getLogName() + " to parent " + newParent.getLogName());

        logger.debug("Checking cyclic dependencies");
        if(checkCyclicNotes(newParent, note)) {
            popupService.showInfoPopup("Cannot move note", "Cannot move \"" + note.getTitle() +
                    "\" because \"" + newParent.getTitle() + "\" is its descendant.");
            return false;
        }

        Optional<String> optional = restService.moveNote(mappingService.mapToNoteMoveDto(note, newParent));

        if(optional.isEmpty()) return false;

        note.getParent().getChildren().remove(note);

        newParent.getChildren().add(note);

        newParent.getChildren().sort(Comparator.naturalOrder());

        TreeItem<Note> noteItem = note.getItem();

        TreeItem<Note> newParentItem = newParent.getItem();

        noteItem.getParent().getChildren().remove(noteItem);

        newParentItem.getChildren().add(noteItem);

        newParentItem.getChildren().sort(Comparator.comparing(TreeItem::getValue));

        context.getNoteTreeChangeSubscription().call();

        return true;
    }

    @Override
    public void removeNote(Note note, boolean removeChildren) {
        logger.info("Attempting to remove note " + note.getLogName() +
                (removeChildren ? " and all its children" : " and move its children to its parent"));
        Optional<String> optional = restService.removeNote(note.getUuid(), removeChildren);

        if(optional.isEmpty()) return;

        note.setRemoved(true);

        TreeItem<Note> noteItem = note.getItem();

        if(!removeChildren) {
            logger.debug("Moving children to parent " + note.getParent().getLogName());

            note.getParent().getChildren().addAll(note.getChildren());

            noteItem.getParent().getChildren().addAll(noteItem.getChildren());
        } else {
            logger.debug("Removing children recursively");

            removeChildrenRecursive(note);
        }

        context.getNotes().remove(note.getUuid());

        note.getParent().getChildren().remove(note);

        noteItem.getParent().getChildren().remove(noteItem);

        context.getNoteTreeChangeSubscription().call();
    }

    private void removeChildrenRecursive(Note note) {
        note.getChildren().forEach(child -> {
            logger.debug("Removing child " + note.getLogName());

            context.getNotes().remove(child.getUuid());

            removeChildrenRecursive(child);
        });
    }

    private boolean checkCyclicNotes(Note checked, Note current) {
        if(current.getChildren().contains(checked)) {
            logger.info("Cyclic note tree while moving " + checked.getLogName() + " detected at note " + current.getLogName());
            return true;
        }

        logger.debug("Current note is " + current.getLogName());

        return current.getChildren().stream().anyMatch(note -> checkCyclicNotes(checked, note));
    }

    private Map<String, Note> notesFromSpace(Space space) {
        return flatMapNotes(space.getRoot()).collect(Collectors.toMap(Note::getUuid, n -> n));
    }

    private Stream<Note> flatMapNotes(Note note) {
        return Stream.concat(Stream.of(note), note.getChildren().stream().flatMap(this::flatMapNotes));
    }

    private TreeItem<Note> buildSpace(Space space) {
        return buildItem(space.getRoot());
    }

    private TreeItem<Note> buildItem(Note note) {
        TreeItem<Note> item = new TreeItem<>(note);
        note.setItem(item);
        item.getChildren().addAll(note.getChildren().stream().sorted().map(this::buildItem).toList());
        return item;
    }
}
