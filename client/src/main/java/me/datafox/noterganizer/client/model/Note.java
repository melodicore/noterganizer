package me.datafox.noterganizer.client.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.*;

import java.util.List;

/**
 * Contains all note data.
 *
 * @author datafox
 */
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Note implements Comparable<Note> {
    @Getter
    @EqualsAndHashCode.Include
    private final String uuid;

    private final StringProperty title;

    private final StringProperty content;

    @ToString.Exclude
    @Getter
    @Setter
    private String lastContent;

    @ToString.Exclude
    private final ObjectProperty<Note> parent;

    @Getter
    @ToString.Exclude
    private final ObservableList<Note> children;

    @Getter
    @Setter
    @ToString.Exclude
    private TreeItem<Note> item;

    @Getter
    @Setter
    private transient boolean removed;

    @Builder
    Note(@NonNull String uuid, String title, String content, Note parent, @Singular List<Note> children) {
        this.uuid = uuid;
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        lastContent = content;
        this.parent = new SimpleObjectProperty<>(parent);
        this.children = FXCollections.observableArrayList(children);
        removed = false;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public StringProperty contentProperty() {
        return content;
    }

    public Note getParent() {
        return parent.get();
    }

    public void setParent(Note parent) {
        this.parent.set(parent);
    }

    public ObjectProperty<Note> parentProperty() {
        return parent;
    }

    public static NoteBuilder builder() {
        return new NoteBuilder();
    }

    public String getLogName() {
        return getTitle() + " (" + getUuid() + ")";
    }

    @Override
    public int compareTo(Note o) {
        return getTitle().compareTo(o.getTitle());
    }
}
