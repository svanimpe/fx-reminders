package ui.main;

import application.Model;
import exception.DAOException;
import dao.RemindersDAO;
import ui.dialog.Dialogs;
import entity.Reminder;
import java.text.SimpleDateFormat;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.util.Icons;

public class ReminderCell extends ListCell<Reminder> {

    private Reminder reminder;
    
    private HBox content;
    private Label title;
    private Label dueDate;
    private Node edit;
    private Node location;
    private Node delete;
    
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public ReminderCell() {
        VBox titleAndDate = new VBox(10);
        titleAndDate.setAlignment(Pos.CENTER_LEFT);
        title = new Label("Title");
        dueDate = new Label("Due date");
        dueDate.getStyleClass().add("date");
        titleAndDate.getChildren().addAll(title, dueDate);
        HBox.setHgrow(titleAndDate, Priority.ALWAYS);
        
        edit = LabelBuilder.create()
                .text(Icons.EDIT)
                .styleClass("action-icon")
                .build();
        edit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Model.getInstance().setEditingReminder(reminder);
                Dialogs.showReminderDialog();
                if (Model.getInstance().hasException()) {
                    Dialogs.showErrorDialog();
                } else {
                    updateItem(Model.getInstance().getEditingReminder(), false);
                }
            }
        });
        HBox.setHgrow(edit, Priority.NEVER);
        
        location = LabelBuilder.create()
                .text(Icons.LOCATION)
                .styleClass("action-icon")
                .build();
        location.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Model.getInstance().setEditingReminder(reminder);
                Dialogs.showLocationDialog();
                if (Model.getInstance().hasException()) {
                    Dialogs.showErrorDialog();
                }
            }
        });
        HBox.setHgrow(location, Priority.NEVER);
        
        delete = LabelBuilder.create()
                .text(Icons.DELETE)
                .styleClass("action-icon")
                .build();
        delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                try {
                    RemindersDAO.getInstance().removeReminder(reminder);
                    Model.getInstance().getReminders().remove(reminder);
                } catch (DAOException ex) {
                    Model.getInstance().addException(ex);
                    Dialogs.showErrorDialog();
                }
            }
        });
        HBox.setHgrow(delete, Priority.NEVER);
        
        content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(titleAndDate, edit, location, delete);
    }
    
    @Override
    protected void updateItem(Reminder r, boolean empty) {
        super.updateItem(r, empty);
        
        getStyleClass().removeAll("first-cell", "regular-cell", "last-cell", "only-cell");
        
        if (!empty) {
            reminder = r;
            
            title.setText(reminder.getTitle());
                       
            if (reminder.getDueDate() != null) {
                dueDate.setText(formatter.format(reminder.getDueDate().getTime()));
            } else {
                dueDate.setText("No date specified");
            }
            
            edit.setVisible(isSelected());
            location.setVisible(isSelected());
            delete.setVisible(isSelected());
            
            setGraphic(content);
            
            if (Model.getInstance().getReminders().size() == 1) {
                getStyleClass().add("only-cell");
            } else if (!Model.getInstance().getReminders().isEmpty() && r == Model.getInstance().getReminders().get(0)) {
                getStyleClass().add("first-cell");
            } else if (!Model.getInstance().getReminders().isEmpty() && r == Model.getInstance().getReminders().get(Model.getInstance().getReminders().size() - 1)) {
                getStyleClass().add("last-cell");
            } else {
                getStyleClass().add("regular-cell");
            }
        } else {
            setGraphic(null);
        }
    }

    @Override
    public void updateSelected(boolean bln) {
        super.updateSelected(bln);

        edit.setVisible(isSelected());
        location.setVisible(isSelected());
        delete.setVisible(isSelected());
    }
}
