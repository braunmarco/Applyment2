package my.cvmanager.repositories;

import org.hibernate.Session;
import jakarta.persistence.criteria.CriteriaQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class BaseDao<T> implements ICrudService<T> {
    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public BaseDao() {
        entityManager = getEntityManager();
    }

    public EntityManager getEntityManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("cvmanagerPU");

        return entityManagerFactory.createEntityManager();
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void persist(T entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        entityManager.getTransaction().begin();
        T result = entityManager.find(clazz, id);
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
    }

    @Override
    public T update(T entity) {
        entityManager.getTransaction().begin();
        T result = entityManager.merge(entity);
        entityManager.getTransaction().commit();
        entityManager.close();

        return result;
    }

    @Override
    public void delete(T entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public List<T> loadAll(Class<T> clazz, String query) {
        entityManager.getTransaction().begin();
        List<T> result = entityManager.createQuery(query, clazz).getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();

        return result;
    }

    /*public List<T> loadAll(final DetachedCriteria detachedCriteria) {
        Session session = entityManager.unwrap(Session.class);
        entityManager.getTransaction().begin();
        List<T> result = detachedCriteria.getExecutableCriteria(session).list();
        entityManager.getTransaction().commit();
        entityManager.close();

        return result;
    }

    @SuppressWarnings("unchecked")
    public T findOne(final DetachedCriteria detachedCriteria) {
        Session session = entityManager.unwrap(Session.class);
        entityManager.getTransaction().begin();
        Optional<T> result = detachedCriteria.getExecutableCriteria(session).list().stream().findFirst();
        entityManager.getTransaction().commit();
        entityManager.close();

        return result.orElse(null);
    }*/
}