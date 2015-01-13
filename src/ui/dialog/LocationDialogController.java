package ui.dialog;

import application.Model;
import dao.RemindersDAO;
import entity.Location;
import exception.DAOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ProgressBar;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import ui.util.Icons;

public class LocationDialogController implements Initializable {

    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private WebView webView;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button saveButton;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progressBar.progressProperty().bind(webView.getEngine().getLoadWorker().progressProperty());
        
        // The WebView is arranged in a StackPane, above the progress bar.
        // So I simply hide/show the WebView to show the progress bar.
        webView.setVisible(false);
        
        webView.getEngine().load(getClass().getResource("/ui/locationpicker/locationpicker.html").toExternalForm());
        
        // Initialisation of JavaScript needs to happen AFTER the page is loaded.
        // That's why I attach a listener to the LoadWorker's state, and put the code there.
        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                if (newState == State.SUCCEEDED) {
                    
                    // Get the global object.
                    JSObject window = (JSObject) webView.getEngine().executeScript("window");
                    
                    // Inject the visibleProperty of the button.
                    window.setMember("hasLocation", deleteButton.visibleProperty());
                    
                    // Inject the currently attached Location, if there is any.
                    if (Model.getInstance().getEditingReminder().getLocation() != null) {
                        window.setMember("existingLocation", Model.getInstance().getEditingReminder().getLocation());
                    }
                    
                    webView.getEngine().executeScript("loadMap()");
                    webView.setVisible(true);
                }
            }
        });
        
        // Print alerts to the console.
        webView.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> alert) {
                System.out.println("JS: " + alert.getData());
            }
        });
        
        if (Model.getInstance().getEditingReminder().getLocation() != null) {
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
        webView.getEngine().executeScript("clearMarker()");
    }
    
    public void cancelAction() {
        webView.getScene().getWindow().hide();
    }
    
    public void saveAction() {
        boolean markerSet = (Boolean)webView.getEngine().executeScript("marker != null");
        
        if (markerSet) {
            if (Model.getInstance().getEditingReminder().getLocation() == null) {
                Model.getInstance().getEditingReminder().setLocation(new Location());
                JSObject window = (JSObject) webView.getEngine().executeScript("window");
                window.setMember("existingLocation", Model.getInstance().getEditingReminder().getLocation());
            }
            webView.getEngine().executeScript("saveMarker()");
        } else {
            Model.getInstance().getEditingReminder().setLocation(null);
        }
        
        try {
            RemindersDAO.getInstance().updateReminder(Model.getInstance().getEditingReminder());
        } catch (DAOException ex) {
            Model.getInstance().addException(ex);
        }
        webView.getScene().getWindow().hide();
    }
}