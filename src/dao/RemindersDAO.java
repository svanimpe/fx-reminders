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
 * Data Access Object for reminders.
 */
public class RemindersDAO {

    public List<Reminder> findAllReminders() throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            List<Reminder> results = em.createNamedQuery("Reminder.findAll", Reminder.class).getResultList();
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
    
    public Reminder findReminder(long id) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Reminder> q = em.createNamedQuery("Reminder.findById", Reminder.class);
            q.setParameter("id", id);
            return q.getSingleResult();
        } catch (NoResultException ex) {
            Logger.getLogger(RemindersDAO.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
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
    
    public List<Reminder> findRemindersInGroup(long groupId) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            TypedQuery<Reminder> q = em.createNamedQuery("Reminder.findByGroup", Reminder.class);
            q.setParameter("groupid", groupId);
            return q.getResultList();
        } catch (PersistenceException ex) {
            throw new DAOException("Could not execute query: " + ex.getMessage(), ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public List<Reminder> findRemindersInGroup(Group g) throws DAOException {
        return findRemindersInGroup(g.getId());
    }
    
    public void addReminder(Reminder r) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(r);        
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
            
            throw new DAOException("Could not add reminder: " + message, ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public void updateReminder(Reminder r) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(r);        
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
            
            throw new DAOException("Could not update reminder: " + message, ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public void removeReminder(long id) throws DAOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RemindersPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            Reminder r = em.find(Reminder.class, id);
            if (r != null) {
                em.remove(r);
            }
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            throw new DAOException("Could not remove reminder: " + ex.getMessage(), ex, true);
        } finally {
            if (em != null) {
                em.close();
            }
            emf.close();
        }
    }
    
    public void removeReminder(Reminder r) throws DAOException {
        removeReminder(r.getId());
    }
    
    /* Singleton */
    
    private RemindersDAO() {
    }
    
    private static final RemindersDAO instance = new RemindersDAO();

    public static RemindersDAO getInstance() {
        return instance;
    }
}
