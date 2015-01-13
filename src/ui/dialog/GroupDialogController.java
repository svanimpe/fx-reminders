package ui.dialog;

import application.Model;
import dao.GroupsDAO;
import entity.Group;
import exception.DAOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ui.util.Icons;

public class GroupDialogController implements Initializable {

    @FXML
    private GridPane inputForm;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button saveButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        if (Model.getInstance().getEditingGroup() != null) {
            titleField.setText(Model.getInstance().getEditingGroup().getTitle());
        } else {
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
            GroupsDAO.getInstance().removeGroup(Model.getInstance().getEditingGroup());
            
            // Once the group is deleted from the database, it is deleted from the model as well
            // and a new group is selected (if there is one).
            
            int index = Model.getInstance().getGroups().indexOf(Model.getInstance().getEditingGroup());
            Model.getInstance().getGroups().remove(Model.getInstance().getEditingGroup());
            
            if (index < Model.getInstance().getGroups().size()) {
                Model.getInstance().setSelectedGroup(Model.getInstance().getGroups().get(index));
            } else if (Model.getInstance().getGroups().size() > 0) {
                Model.getInstance().setSelectedGroup(Model.getInstance().getGroups().get(Model.getInstance().getGroups().size() - 1));
            } else {
                Model.getInstance().setSelectedGroup(null);
            }
        } catch (DAOException ex) {
            Model.getInstance().addException(ex);
        }
        
        inputForm.getScene().getWindow().hide();
    }
    
    public void cancelAction() {
        inputForm.getScene().getWindow().hide();
    }
    
    public void saveAction() {
        
        // Remove all the warning icons from previous checks before checking again.
        for (Node node : inputForm.lookupAll(".warning-icon")) {
            inputForm.getChildren().remove(node);
        }
        
        // Check if the title is non-empty.
        if (titleField.getText().trim().isEmpty()) {
            inputForm.add(LabelBuilder.create()
                    .text(Icons.INVALID_INPUT)
                    .styleClass("warning-icon")
                    .build(), 2, 0);
            inputForm.getScene().getWindow().sizeToScene();

            titleField.setText("This title is too short");
            titleField.selectAll();
            titleField.requestFocus();
            return;
        }
        
        // If the user is creating a new group, or renaming an existing one, check to see if the title's available.
        if (Model.getInstance().getEditingGroup() == null || !Model.getInstance().getEditingGroup().getTitle().equals(titleField.getText().trim())) {
            try {
                if (!GroupsDAO.getInstance().isTitleAvailable(titleField.getText().trim())) {
                    inputForm.add(LabelBuilder.create()
                            .text(Icons.INVALID_INPUT)
                            .styleClass("warning-icon")
                            .build(), 2, 0);
                    inputForm.getScene().getWindow().sizeToScene();
                    
                    titleField.setText("This title is already taken");
                    titleField.selectAll();
                    titleField.requestFocus();
                    return;
                }
            } catch (DAOException ex) {
                Model.getInstance().addException(ex);
                inputForm.getScene().getWindow().hide();
                return;
            }
        }
        
        // Create a new group, or update the existing one.
        if (Model.getInstance().getEditingGroup() == null) {
            Group group = new Group();
            group.setTitle(titleField.getText().trim());
            try {
                GroupsDAO.getInstance().addGroup(group);
                Model.getInstance().getGroups().add(group);
                Model.getInstance().setSelectedGroup(group);
            } catch (DAOException ex) {
                Model.getInstance().addException(ex);
            }
        } else {
            Group group = Model.getInstance().getEditingGroup();
            String oldTitle = group.getTitle();
            group.setTitle(titleField.getText().trim());
            try {
                GroupsDAO.getInstance().updateGroup(group);
            } catch (DAOException ex) {
                Model.getInstance().addException(ex);
                // If something went wrong, revert to the previous state.
                group.setTitle(oldTitle);
            }
        }
        
        inputForm.getScene().getWindow().hide();
    }
}