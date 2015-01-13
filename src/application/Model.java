package application;

import exception.DAOException;
import dao.GroupsDAO;
import dao.RemindersDAO;
import entity.Group;
import entity.Reminder;
import java.util.LinkedList;
import java.util.Queue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Window;
import exception.RemindersException;
import java.util.ArrayList;
import java.util.List;
import ui.util.SelectionListener;

/*
 * Holds everything the UI needs to work.
 * The actual data is loaded via the DAO objects.
 */
public class Model {

    /*
     * Initialize the model. This will load the groups from the database, as
     * well as the reminders in the first group (if there is one).
     * If something goes wrong here, the exception is queued and the group
     * and/or reminder list starts out empty.
     */
    public void initialize() {
        try {
            groups = FXCollections.observableArrayList(GroupsDAO.getInstance().findAllGroups());
        } catch (DAOException ex) {
            addException(ex);
            groups = FXCollections.observableArrayList();
        }
        
        if (!groups.isEmpty()) {
            selectedGroup = groups.get(0);
            try {
                reminders = FXCollections.observableArrayList(RemindersDAO.getInstance().findRemindersInGroup(selectedGroup));
            } catch (DAOException ex) {
                addException(ex);
                reminders = FXCollections.observableArrayList();
            }
        } else {
            selectedGroup = null;
            reminders = FXCollections.observableArrayList();
        }
        
        editingGroup = null;
        editingReminder = null;
    }
    
    /*
     * The groups to be displayed in the ListView. These groups are stored in an
     * ObservableList, so the ListView can bind to them.
     */
    private ObservableList<Group> groups;
    
    public ObservableList<Group> getGroups() {
        return groups;
    }
    
    /*
     * The currently selected group. Updating the selection will load the
     * reminders in the newly selected group.
     */   
    private Group selectedGroup;

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        if (selectedGroup != this.selectedGroup) {
            this.selectedGroup = selectedGroup;

            reminders.clear();
            if (selectedGroup != null) {
                try {
                    reminders.addAll(RemindersDAO.getInstance().findRemindersInGroup(selectedGroup));
                } catch (DAOException ex) {
                    addException(ex);
                }
            }
            
            notifySelectionListeners(selectedGroup);
        }
    }
    
    /*
     * Objects can listen to changes in the selection. The UI will do this, to
     * update the ListView in case the selection is changed elsewhere in the
     * application. This is the case, for example, when groups are added or
     * deleted.
     */
    private List<SelectionListener> selectionListeners = new ArrayList<>();
    
    public void addSelectionListener(SelectionListener listener) {
        selectionListeners.add(listener);
    }
    
    public void removeSelectionListener(SelectionListener listener) {
        selectionListeners.remove(listener);
    }
    
    public void notifySelectionListeners(Group newSelection) {
        for (SelectionListener listener : selectionListeners) {
            listener.selectionChanged(newSelection);
        }
    }
    
    /*
     * The group that is currently being edited.
     * Set this before opening the group dialog.
     */
    private Group editingGroup;

    public Group getEditingGroup() {
        return editingGroup;
    }

    public void setEditingGroup(Group editingGroup) {
        this.editingGroup = editingGroup;
    }
    
    /*
     * The reminders to be displayed in the ListView. These reminders are stored
     * in an ObservableList, so the ListView can bind to them.
     */
    private ObservableList<Reminder> reminders;
    
    public ObservableList<Reminder> getReminders() {
        return reminders;
    }
    
    /*
     * The reminder that is currently being edited.
     * Set this before opening the reminder dialog.
     */
    private Reminder editingReminder;

    public Reminder getEditingReminder() {
        return editingReminder;
    }

    public void setEditingReminder(Reminder editingReminder) {
        this.editingReminder = editingReminder;
    }
    
    /*
     * A reference to the main window. This can come in handy when working with
     * dialog windows.
     */
    private Window mainWindow;

    public Window getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(Window mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    /*
     * Exceptions that occur are stored in a queue and handled later by the
     * exceptions dialog.
     */
    private Queue<RemindersException> exceptions = new LinkedList<>();

    public boolean hasException() {
        return !exceptions.isEmpty();
    }

    public void addException(RemindersException exception) {
        exceptions.add(exception);
    }
    
    public RemindersException getNextException() {
        return exceptions.remove();
    }
    
    /*
     * There can be only one!
     */
    
    private Model() { }
    
    private static final Model instance = new Model();
    
    public static Model getInstance() {
        return instance;
    }
}
