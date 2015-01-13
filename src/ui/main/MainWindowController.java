package ui.main;

import ui.util.SelectionListener;
import application.Model;
import entity.Group;
import entity.Reminder;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import ui.util.Icons;
import ui.dialog.Dialogs;

public class MainWindowController implements Initializable {

    @FXML
    private ListView<Group> groupList;
    
    @FXML
    private ListView<Reminder> reminderList;
    
    @FXML
    private Button addGroupButton;
    
    @FXML
    private Button addReminderButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        groupList.setItems(Model.getInstance().getGroups());
        groupList.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
            @Override
            public ListCell<Group> call(ListView<Group> p) {
                return new GroupCell();
            }
        });

        if (Model.getInstance().getSelectedGroup() != null) {
            groupList.getSelectionModel().select(Model.getInstance().getSelectedGroup());
        } else {
            addReminderButton.setDisable(true);
        }
        
        Model.getInstance().addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(Group newSelection) {
                if (newSelection != null) {
                    groupList.getSelectionModel().select(newSelection);
                }
                addReminderButton.setDisable(newSelection == null);
            }
        });
        
        groupList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Group>() {
            @Override
            public void changed(ObservableValue<? extends Group> ov, Group oldValue, Group newValue) {
                Model.getInstance().setSelectedGroup(newValue);
            }
        });
        
        reminderList.setItems(Model.getInstance().getReminders());
        reminderList.setCellFactory(new Callback<ListView<Reminder>, ListCell<Reminder>>() {
            @Override
            public ListCell<Reminder> call(ListView<Reminder> p) {
                return new ReminderCell();
            }
        });
        
        addGroupButton.setGraphic(LabelBuilder.create()
                .text(Icons.ADD)
                .styleClass("button-icon")
                .build());
        addReminderButton.setGraphic(LabelBuilder.create()
                .text(Icons.ADD)
                .styleClass("button-icon")
                .build());
    }
    
    public void addGroupAction() {
        // Set the editingGroup to null, to indicate to the dialog that a new group should be created.
        Model.getInstance().setEditingGroup(null);
        Dialogs.showGroupDialog();
        // Always check for errors after using a dialog window.
        if (Model.getInstance().hasException()) {
            Dialogs.showErrorDialog();
        }
    }
    
    public void addReminderAction() {
        Model.getInstance().setEditingReminder(null);
        Dialogs.showReminderDialog();
        if (Model.getInstance().hasException()) {
            Dialogs.showErrorDialog();
        }
    }
}
