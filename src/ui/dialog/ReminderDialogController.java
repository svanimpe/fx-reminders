package ui.dialog;

import application.Model;
import dao.RemindersDAO;
import entity.Group;
import entity.Reminder;
import exception.DAOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import jfxtras.scene.control.CalendarTextField;
import ui.util.Icons;

public class ReminderDialogController implements Initializable {

    @FXML
    private GridPane inputForm;
    
    @FXML
    private ChoiceBox<Group> groupBox;
    
    @FXML
    private TextField titleField;
    
    private CalendarTextField dateField;
    
    @FXML
    private TextArea notesArea;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button saveButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        groupBox.setItems(Model.getInstance().getGroups());
        
        // I remove the TextArea and add it back later, because the focus order depends
        // on the order in which the nodes are added to the pane.
        inputForm.getChildren().remove(notesArea);
        
        dateField = new CalendarTextField();
        // A week starts on monday, not sunday (crazy Americans ...).
        dateField.setLocale(Locale.UK);
        dateField.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
        dateField.setShowTime(true);
        inputForm.add(dateField, 1, 2);
        
        inputForm.getChildren().add(notesArea);
        
        if (Model.getInstance().getEditingReminder() != null) {
            groupBox.getSelectionModel().select(Model.getInstance().getEditingReminder().getGroup());
            titleField.setText(Model.getInstance().getEditingReminder().getTitle());
            dateField.setCalendar(Model.getInstance().getEditingReminder().getDueDate());
            notesArea.setText(Model.getInstance().getEditingReminder().getNotes());
        } else {
            groupBox.getSelectionModel().select(Model.getInstance().getSelectedGroup());
            deleteButton.setVisible(false);
        }
        
        deleteButton.setGraphic(LabelBuilder.create()
                .text(Icons.DELETE)
                .styleClass("button-icon")
                .build());
        saveButton.setGraphic(LabelBuilder.create()
                .text(Icons.SAVE)
                .styleClass("button-icon")
                .build());
    }
    
    public void deleteAction() {
        try {
            RemindersDAO.getInstance().removeReminder(Model.getInstance().getEditingReminder());
            Model.getInstance().getReminders().remove(Model.getInstance().getEditingReminder());
        } catch (DAOException ex) {
            Model.getInstance().addException(ex);
        }
        
        inputForm.getScene().getWindow().hide();
    }
    
    public void cancelAction() {
        inputForm.getScene().getWindow().hide();
    }
    
    public void saveAction() {
        for (Node node : inputForm.lookupAll(".warning-icon")) {
            inputForm.getChildren().remove(node);
        }
        
        if (titleField.getText().trim().isEmpty()) {
            inputForm.add(LabelBuilder.create()
                    .text(Icons.INVALID_INPUT)
                    .styleClass("warning-icon")
                    .build(), 2, 1);
            inputForm.getScene().getWindow().sizeToScene();
            
            titleField.setText("This title is too short");
            titleField.selectAll();
            titleField.requestFocus();
            return;
        }
        
        if (Model.getInstance().getEditingReminder() == null) {
            Reminder reminder = new Reminder();
            reminder.setGroup(groupBox.getValue());
            reminder.setTitle(titleField.getText().trim());
            reminder.setDueDate(dateField.getCalendar());
            reminder.setNotes(notesArea.getText());
            
            try {
                RemindersDAO.getInstance().addReminder(reminder);
                if (reminder.getGroup() == Model.getInstance().getSelectedGroup()) {
                    Model.getInstance().getReminders().add(reminder);
                }
            } catch (DAOException ex) {
                Model.getInstance().addException(ex);
            }
        } else {
            Reminder reminder = Model.getInstance().getEditingReminder();
            Group oldGroup = reminder.getGroup();
            String oldTitle = reminder.getTitle();
            Calendar oldDate = reminder.getDueDate();
            String oldNotes = reminder.getNotes();
            
            reminder.setGroup(groupBox.getValue());
            reminder.setTitle(titleField.getText().trim());
            reminder.setDueDate(dateField.getCalendar());
            reminder.setNotes(notesArea.getText());
            
            try {
                RemindersDAO.getInstance().updateReminder(reminder);
                if (reminder.getGroup() != Model.getInstance().getSelectedGroup()) {
                    Model.getInstance().getReminders().remove(reminder);
                }
            } catch (DAOException ex) {
                Model.getInstance().addException(ex);
                reminder.setGroup(oldGroup);
                reminder.setTitle(oldTitle);
                reminder.setDueDate(oldDate);
                reminder.setNotes(oldNotes);
            }
        }
        
        inputForm.getScene().getWindow().hide();
    }
}