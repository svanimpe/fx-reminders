package entity;

import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
 * A reminder, consisting of an id, a non-empty title, an optional due date, an
 * optional location and optional notes. A reminder also has a many-to-one
 * relationship with it's owning group.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Reminder.findAll", query = "SELECT r FROM Reminder r"),
    @NamedQuery(name = "Reminder.findById", query = "SELECT r FROM Reminder r WHERE r.id = :id"),
    @NamedQuery(name = "Reminder.findByGroup", query = "SELECT r FROM Reminder r JOIN r.group g WHERE g.id = :groupid")       
})
public class Reminder {
    
    /*
     * I customized the generator in the same way as I did in Group.
     */
    @Id @GeneratedValue(generator = "REMINDER_ID") @TableGenerator(table = "ID_GEN", name = "REMINDER_ID", allocationSize = 1)
    @Min(value = 1, message = "a reminder's id must be greater than zero")
    private long id;
    
    /*
     * Again, because GROUP is a reserved SQL keyword, I renamed the join column
     * to REMINDER_GROUP.
     */
    @ManyToOne @JoinColumn(name = "REMINDER_GROUP")
    @NotNull(message = "a reminder must be assigned to a group")
    private Group group;
    
    @NotNull(message = "a reminder's title cannot be empty")
    @Size(min = 1, message = "a reminder's title cannot be empty")
    private String title;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dueDate;

    // Note: NetBeans doesn't seem to know about embedded objects, so if you get
    // warnings here, just ignore them. It works fine.
    private Location location;
    
    private String notes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reminder other = (Reminder) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return title;
    }
}
