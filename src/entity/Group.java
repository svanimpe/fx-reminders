package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
 * A group of reminders. Every group has an id and a unique and non-empty title.
 * 
 * The table name is changed to TBL_GROUP because GROUP is a reserved keyword in
 * SQL. Because the relationship between Reminder and Group is only
 * one-directional, queries "Group.findSizeById" and "Reminder.findByGroup" are
 * provided to find the reminders in a particular group.
 */
@Entity @Table(name = "TBL_GROUP")
@NamedQueries({
    @NamedQuery(name = "Group.findAll", query = "SELECT g FROM Group g"),
    @NamedQuery(name = "Group.findById", query = "SELECT g FROM Group g WHERE g.id = :id"),
    @NamedQuery(name = "Group.findSizeById", query = "SELECT COUNT(r) FROM Reminder r JOIN r.group g WHERE g.id = :id"),
    @NamedQuery(name = "Group.checkTitle", query = "SELECT COUNT(g) FROM Group g WHERE g.title = :title")
})
public class Group {

    /*
     * I customized the generator to get a separate counter per table, and to
     * allocate only one ID at a time. This is done because in this example, the
     * entity manager (factory) is opened and closed before and after every
     * operation, so there is no need to allocate more than one ID at a time.
     */
    @Id @GeneratedValue(generator = "GROUP_ID") @TableGenerator(table = "ID_GEN", name = "GROUP_ID", allocationSize = 1)
    @Min(value = 1, message = "a group's id must be greater than zero")
    private long id;
    
    @Column(unique = true)
    @NotNull(message = "a group's title cannot be empty")
    @Size(min = 1, message = "a group's title cannot be empty")
    private String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Group other = (Group) obj;
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
