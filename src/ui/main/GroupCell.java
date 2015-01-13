package ui.main;

import application.Model;
import ui.dialog.Dialogs;
import entity.Group;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ui.util.Icons;

public class GroupCell extends ListCell<Group> {

    private Group group;
    
    private HBox content;
    private Label title;
    private Node edit;
    
    public GroupCell() {
        title = new Label("Title");
        // A Label won't grow unless you change it's maxWidth
        title.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(title, Priority.ALWAYS);
        
        edit = LabelBuilder.create()
                .text(Icons.EDIT)
                .styleClass("action-icon")
                .build();
        edit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                // Don't forget to set the group to edit in the model, before opening the dialog.
                Model.getInstance().setEditingGroup(group);
                Dialogs.showGroupDialog();
                // And check for errors afterwards.
                if (Model.getInstance().hasException()) {
                    Dialogs.showErrorDialog();
                } else {
                    // If there were no errors, update the cell with the new information.
                    updateItem(Model.getInstance().getEditingGroup(), false);
                }
            }
        });
        HBox.setHgrow(edit, Priority.NEVER);
        
        content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(title, edit);
    }
    
    @Override
    protected void updateItem(Group g, boolean empty) {
        super.updateItem(g, empty);

        // Remove all custom classes first, and add the right ones later on.
        getStyleClass().removeAll("first-cell", "regular-cell", "last-cell", "only-cell");
        
        if (!empty) {
            group = g;
            title.setText(group.getTitle());
            edit.setVisible(isSelected());
            setGraphic(content);
            
            // Because JavaFX CSS does not support pseudoclasses like first-child or last-child,
            // I created some custom classes, and add them programmatorically.
            
            if (Model.getInstance().getGroups().size() == 1) {
                getStyleClass().add("only-cell");
            } else if (!Model.getInstance().getGroups().isEmpty() && g == Model.getInstance().getGroups().get(0)) {
                getStyleClass().add("first-cell");
            } else if (!Model.getInstance().getGroups().isEmpty() && g == Model.getInstance().getGroups().get(Model.getInstance().getGroups().size() - 1)) {
                getStyleClass().add("last-cell");
            } else {
                getStyleClass().add("regular-cell");
            }
        } else {
            setGraphic(null);
        }
    }

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
        edit.setVisible(selected);
    }
}
