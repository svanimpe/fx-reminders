package ui.util;

import entity.Group;

/*
 * A selection listener for the group list.
 */
public interface SelectionListener {
    void selectionChanged(Group newSelection);
}
