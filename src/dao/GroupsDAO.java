package dao;

import exception.DAOException;
import entity.Group;
import entity.Reminder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;

/*
 * Data Access Object for groups.
 * 
 * Some general notes:
 * - I open and close an entity manager (factory) before and after every
 *   operation. I'm not sure if this is the best way to do it, but it's easy and
 *   it works.
 * - Every operation that updates the database is wrapped in a transaction.
 * - All PersistenceExceptions and ConstraintViolationExceptions are caught and
 *   wrapped as DAOExceptions.
 */
public class GroupsDAO {
        
    public List<Group> findAllGroups() throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            List<Group> results = em.createNamedQuery("Group.findAll", Group.class).getResultList();
            return results;
        } catch (PersistenceException ex) {
            throw new DAOException("Could not execute query: " + ex.getMessage(), ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }

    public Group findGroup(long id) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Group> q = em.createNamedQuery("Group.findById", Group.class);
            q.setParameter("id", id);
            return q.getSingleResult();
        } catch (NoResultException ex) {
            // This isn't really an exception, so I simply log it and return null.
            Logger.getLogger(GroupsDAO.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
            return null;
        } catch (PersistenceException ex) {
            throw new DAOException("Could not execute query: " + ex.getMessage(), ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public long findGroupSize(long id) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Long> q = em.createNamedQuery("Group.findSizeById", Long.class);
            q.setParameter("id", id);
            return q.getSingleResult();
        } catch (PersistenceException ex) {
            throw new DAOException("Could not execute query: " + ex.getMessage(), ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public boolean isTitleAvailable(String title) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;        
        try {
            em = emf.createEntityManager();
            TypedQuery<Long> q = em.createNamedQuery("Group.checkTitle", Long.class);
            q.setParameter("title", title);
            return q.getSingleResult() == 0;
        } catch (PersistenceException ex) {
            throw new DAOException("Could not execute query: " + ex.getMessage(), ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public void addGroup(Group g) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(g);        
            em.getTransaction().commit();
        } catch (ConstraintViolationException | PersistenceException ex) {
            String message = ex.getMessage();
            
            // Here I loop over all the nested exceptions and see if any of those is a ConstraintViolationException.
            // If so, I use the violations to build a more informative error message.
            Throwable nestedException = ex;
            while (nestedException != null) {
                if (nestedException instanceof ConstraintViolationException) {
                    message = Util.buildErrorMessage((ConstraintViolationException)nestedException);
                    break;
                }
                nestedException = nestedException.getCause();
            }
            
            throw new DAOException("Could not add group: " + message, ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public void updateGroup(Group g) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(g);        
            em.getTransaction().commit();
        } catch (ConstraintViolationException | PersistenceException ex) {
            String message = ex.getMessage();
            
            Throwable nestedException = ex;
            while (nestedException != null) {
                if (nestedException instanceof ConstraintViolationException) {
                    message = Util.buildErrorMessage((ConstraintViolationException)nestedException);
                    break;
                }
                nestedException = nestedException.getCause();
            }
            
            throw new DAOException("Could not update group: " + message, ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public void removeGroup(long id) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            Group g = em.find(Group.class, id);
            if (g != null) {
                // Delete all the reminders in this group, before deleting the group itself.
                List<Reminder> remindersInGroup = RemindersDAO.getInstance().findRemindersInGroup(g);
                for (Reminder r : remindersInGroup) {
                    RemindersDAO.getInstance().removeReminder(r);
                }
                em.remove(g);
            }
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            throw new DAOException("Could not remove group: " + ex.getMessage(), ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public void removeGroup(Group g) throws DAOException {
        removeGroup(g.getId());
    }
    
    /*
     * I implemented this class as a singleton, instead of using static methods.
     * That might ease future refactoring.
     */
    
    private GroupsDAO() {
    }
    
    private static final GroupsDAO instance = new GroupsDAO();

    public static GroupsDAO getInstance() {
        return instance;
    }
}
