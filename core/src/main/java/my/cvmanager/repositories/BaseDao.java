package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base class for Data Access Objects (DAOs).
 *
 * @param <T> the type of the entity
 */
public class BaseDao<T> {

    private final Class<T> entityClass;

    /**
     * Creates a DAO for the given entity type.
     *
     * @param entityClass the type of the entity
     */
    public BaseDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Makes a new entity persistent.
     *
     * @param entity        the entity to persist
     * @param entityManager the EntityManager
     */
    public void persist(T entity, EntityManager entityManager) {
        entityManager.persist(entity);
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id            the ID of the entity
     * @param entityManager the EntityManager
     * @return the entity or an empty Optional
     */
    public Optional<T> findBy(Long id, EntityManager entityManager) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id            the ID of the entity
     * @param entityManager the EntityManager
     * @return the entity, or {@code null} if it does not exist
     */
    public T find(Long id, EntityManager entityManager) {
        return entityManager.find(entityClass, id);
    }

    /**
     * Updates an entity.
     *
     * @param entity        the entity to update
     * @param entityManager the EntityManager
     * @return the managed entity returned by {@link EntityManager#merge(Object)}
     */
    public T update(T entity, EntityManager entityManager) {
        return entityManager.merge(entity);
    }

    /**
     * Removes an entity, merging it first if it is detached.
     *
     * @param entity        the entity to delete
     * @param entityManager the EntityManager
     * @throws NullPointerException if {@code entity} is {@code null}
     */
    public void delete(T entity, EntityManager entityManager) {
        if (entity == null) {
            throw new NullPointerException("Entity is null");
        }
        if (!entityManager.contains(entity)) {
            entity = entityManager.merge(entity);
        }
        entityManager.remove(entity);
    }

    /**
     * Loads all entities.
     *
     * @param entityManager the EntityManager
     * @return a list of all entities
     */
    public List<T> loadAll(EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);

        return entityManager.createQuery(cq).getResultList();
    }

    /**
     * Finds an entity by an attribute.
     *
     * @param attribute     the attribute to search for
     * @param value         the value of the attribute
     * @param entityManager the EntityManager
     * @return the first matching entity, or {@code null} if no match exists
     */
    public T findOne(String attribute, Object value, EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root).where(cb.equal(root.get(attribute), value));

        Optional<T> first = entityManager.createQuery(cq).getResultStream().findFirst();

        //return entityManager.createQuery(cq).getResultStream().findFirst();
        return first.orElse(null);
    }

    /**
     * Finds an entity by multiple attributes.
     *
     * @param params        the attributes and their values
     * @param entityManager the EntityManager
     * @return the first matching entity, or {@code null} if no match exists
     */
    public T findOne(Map<String, Object> params, EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            predicates.add(cb.equal(root.get(entry.getKey()), entry.getValue()));
        }

        cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        Optional<T> first = entityManager.createQuery(cq).getResultStream().findFirst();

        return first.orElse(null);
    }
}