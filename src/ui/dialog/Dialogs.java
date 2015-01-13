package ui.dialog;

import application.Model;
import exception.RemindersException;
import java.io.IOException;
import javafx.animation.FadeTransitionBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ui.util.Icons;

/*
 * Utility class for showing dialog windows.
 */
public class Dialogs {

    /*
     * Just to be clear.
     */
    private Dialogs() {
        throw new UnsupportedOperationException("Uninstantiable class");
    }

    /*
     * A simple callback interface for an action that is to be performed when
     * the dialog is shown.
     */
    private static interface OnShownAction {
        void perform();
    }
    
    /*
     * Show a dialog window with the given node as the root node, and perform
     * the given action when the dialog is shown.
     */
    private static void showDialog(Parent root, final OnShownAction onShown) {
        
        // Create an undecorated modal stage, with the main window as its owner,
        // and the given root as its content
        
        final Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initModality(Modality.WINDOW_MODAL);
        
        final Window owner = Model.getInstance().getMainWindow();
        dialog.initOwner(owner);

        dialog.setScene(new Scene(root));
        
        dialog.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                
                // Center the dialog within the main window.
                dialog.setX(owner.getX() + owner.getWidth() / 2 - dialog.getWidth() / 2);
                dialog.setY(owner.getY() + owner.getHeight() / 2 - dialog.getHeight() / 2);
                
                // Perform the optional callback.
                if (onShown != null) {
                    onShown.perform();
                }
            }
        });

        // Fade out the main window, show the dialog, wait for it the closed,
        // then fade the main window back in.
        
        FadeTransitionBuilder.create()
                .node(owner.getScene().getRoot())
                .toValue(0.25)
                .build()
                .play();

        dialog.showAndWait();

        FadeTransitionBuilder.create()
                .node(owner.getScene().getRoot())
                .toValue(1)
                .build()
                .play();
    }

    /*
     * Create and show a backup error dialog. This is probably overkill, but I
     * created this method just to make sure there always is an error dialog,
     * even when ErrorDialog.fxml cannot be loaded.
     */
    private static void showBackupErrorDialog() {
        final RemindersException exception = Model.getInstance().getNextException();
        
        final VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.getStylesheets().add(Dialogs.class.getResource("/resources/css/styles.css").toExternalForm());
        
        root.getChildren().add(TextBuilder.create().text("Oops...").styleClass("heading").build());
        
        if (exception.isRecoverable()) {
            root.getChildren().add(new Label("Looks like something went wrong here.\nYou can continue working or quit and call it a day."));
        } else {
            root.getChildren().add(new Label("Looks like something went wrong here.\nUnfortunately, the application cannot continue working."));
        }
        
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        
        Button quitButton = ButtonBuilder.create()
                .text("Quit")
                .styleClass("quit-button")
                .graphic(LabelBuilder.create()
                    .text(Icons.QUIT)
                    .styleClass("button-icon")
                    .build())
                .onAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        System.exit(1);
                    }
                })
                .build();
        buttons.getChildren().add(quitButton);
        
        final Button continueButton = ButtonBuilder.create()
                .text("Continue")
                .onAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        root.getScene().getWindow().hide();
                    }
                })
                .build();
                
        if (exception.isRecoverable()) {
            continueButton.setDefaultButton(true);
            buttons.getChildren().add(continueButton);
        } else {
            quitButton.setDefaultButton(true);
        }
        
        root.getChildren().add(buttons);
        
        showDialog(root, new OnShownAction() {
            @Override
            public void perform() {
                for (Node node : root.lookupAll(".button")) {
                    if (node instanceof Button && ((Button)node).isDefaultButton()) {
                        node.requestFocus();
                    }
                }
            }
        });
    }

    /*
     * Show the error dialog once for every exception in the queue.
     */
    public static void showErrorDialog() {
        while (Model.getInstance().hasException()) {
            try {
                final Parent root = FXMLLoader.load(Dialogs.class.getResource("/resources/fxml/ErrorDialog.fxml"));
                showDialog(root, new OnShownAction() {
                    @Override
                    public void perform() {
                        
                        // When the dialog is shown, make sure the default button has focus as well.
                        // If I don't do this, the quit button might get the initial focus,
                        // which is not only risky, it looks weird too.
                        for (Node node : root.lookupAll(".button")) {
                            if (node instanceof Button && ((Button)node).isDefaultButton()) {
                                node.requestFocus();
                            }
                        }
                    }
                });
            } catch (IOException ex) {
                showBackupErrorDialog();
            }
        }
    }

    /*
     * Show the create or edit group dialog.
     */
    public static void showGroupDialog() {
        try {
            final Parent root = FXMLLoader.load(Dialogs.class.getResource("/resources/fxml/GroupDialog.fxml"));
            showDialog(root, new OnShownAction() {
                @Override
                public void perform() {
                    
                    // Give focus to the first text input node.
                    Node first = root.lookup(".text-input");
                    if (first != null) {
                        first.requestFocus();
                    }
                }
            });
        } catch (IOException ex) {
            Model.getInstance().addException(new RemindersException("Could not load /resources/fxml/GroupDialog.fxml", ex, true));
        }
    }

    /*
     * Show the create or edit reminder dialog.
     */
    public static void showReminderDialog() {
        try {
            final Parent root = FXMLLoader.load(Dialogs.class.getResource("/resources/fxml/ReminderDialog.fxml"));
            showDialog(root, new OnShownAction() {
                @Override
                public void perform() {
                    Node first = root.lookup(".text-input");
                    if (first != null) {
                        first.requestFocus();
                    }
                }
            });
        } catch (IOException ex) {
            Model.getInstance().addException(new RemindersException("Could not load /resources/fxml/ReminderDialog.fxml", ex, true));
        }
    }
    
    /*
     * Show the attach a location dialog.
     */
    public static void showLocationDialog() {
        try {
            final Parent root = FXMLLoader.load(Dialogs.class.getResource("/resources/fxml/LocationDialog.fxml"));
            showDialog(root, new OnShownAction() {
                @Override
                public void perform() {
                    for (Node node : root.lookupAll(".button")) {
                        if (node instanceof Button && ((Button) node).isDefaultButton()) {
                            node.requestFocus();
                        }
                    }
                }
            });
        } catch (IOException ex) {
            Model.getInstance().addException(new RemindersException("Could not load /resources/fxml/LocationDialog.fxml", ex, true));
        }
    }
}