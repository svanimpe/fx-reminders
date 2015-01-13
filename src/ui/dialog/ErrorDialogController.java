package ui.dialog;

import application.Model;
import exception.RemindersException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import ui.util.Icons;

public class ErrorDialogController implements Initializable {

    @FXML
    private Label continueMessage;
    
    @FXML
    private TextArea errorMessage;
    
    @FXML
    private HBox buttonBox;
    
    @FXML
    private Button quitButton;
    
    @FXML
    private Button continueButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quitButton.setGraphic(LabelBuilder.create()
                .text(Icons.QUIT)
                .styleClass("button-icon")
                .build());
        
        // Consume the next exception in the queue, see if it's recoverable
        // and update the labels and buttons accordingly.
        
        RemindersException exception = Model.getInstance().getNextException();
        
        if (exception.isRecoverable()) {
            continueMessage.setText("Looks like something went wrong here.\nYou can continue working or quit and call it a day.");
        } else {
            continueMessage.setText("Looks like something went wrong here.\nUnfortunately, the application cannot continue working.");
            buttonBox.getChildren().remove(continueButton);
            quitButton.setDefaultButton(true);
        }
        
        StringWriter error = new StringWriter();
        exception.printStackTrace(new PrintWriter(error));
        errorMessage.setText(error.toString());
    }
    
    public void quitAction() {
        // Note: For some reason, Platform.exit() doesn't do anything here ?
        System.exit(1);
    }
    
    public void continueAction() {
        quitButton.getScene().getWindow().hide();
    }
}