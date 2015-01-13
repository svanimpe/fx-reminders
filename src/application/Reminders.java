package application;

import exception.RemindersException;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPaneBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ui.dialog.Dialogs;

public class Reminders extends Application {
    
    @Override
    public void start(Stage stage) {
        Model.getInstance().initialize();
        Model.getInstance().setMainWindow(stage);
        
        // Load the fonts used. I don't think the actual font size matters here.
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/awesome.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/resources/fonts/marck.ttf"), 14);
        
        // Try to load the main window. If this fails for some reason, an exception is queued and a backup is loaded.
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/resources/fxml/MainWindow.fxml"));
        } catch (IOException ex) {
            Model.getInstance().addException(new RemindersException("Could not load /resources/fxml/MainWindow.fxml", ex, false));
            root = AnchorPaneBuilder.create().stylesheets(getClass().getResource("/resources/css/styles.css").toExternalForm()).id("mainWindow").prefWidth(800).prefHeight(600).build();
        }
        
        Scene scene = new Scene(root, Color.BLACK);
        stage.setScene(scene);
        stage.setTitle("Reminders");
        stage.centerOnScreen();
        
        // Once the stage is shown, check if there were any errors during initialization.
        stage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (Model.getInstance().hasException()) {
                    Dialogs.showErrorDialog();
                }
            }  
        });
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
