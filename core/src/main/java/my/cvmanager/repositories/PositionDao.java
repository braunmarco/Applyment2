package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import my.cvmanager.domain.Position;

import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for {@link Position} entities.
 *
 * <p>Extends the standard CRUD operations of {@link BaseDao} with
 * queries ordered by a position's {@code orderIndex}.</p>
 *
 * @see BaseDao
 * @see Position
 */
public class PositionDao extends BaseDao<Position> {
    private final Logger logger = Logger.getLogger(PositionDao.class.getCanonicalName());

    /**
     * Creates a DAO for {@link Position} entities.
     */
    public PositionDao() {
        super(Position.class);
    }

    /**
     * Sort directions for position queries.
     */
    public enum Order {
        /** Ascending order. */
        UP("ASC"),
        /** Descending order. */
        DOWN("DESC");

        public String order;

        Order(String order) {
            this.order = order;
        }

        /**
         * Returns the JPQL sort direction.
         *
         * @return {@code ASC} or {@code DESC}
         */
        public String code() {
            return order;
        }
    }

    /**
     * Finds all positions in ascending order of {@code orderIndex}.
     *
     * @param entityManager the entity manager used to execute the query
     * @return positions sorted in ascending order
     */
    public List<Position> findAllOrderedUp(EntityManager entityManager) {
        return findAllOrdered(entityManager, Order.UP);
    }

    /**
     * Finds all positions in descending order of {@code orderIndex}.
     *
     * @param entityManager the entity manager used to execute the query
     * @return positions sorted in descending order
     */
    public List<Position> findAllOrderedDown(EntityManager entityManager) {
        return findAllOrdered(entityManager, Order.DOWN);
    }

    /**
     * Finds all positions sorted by {@code orderIndex}.
     *
     * @param entityManager the entity manager used to execute the query
     * @param order the sort direction
     * @return positions sorted in the requested direction
     */
    public List<Position> findAllOrdered(EntityManager entityManager, Order order) {
        return entityManager.createQuery(
                "SELECT p FROM Position p ORDER BY p.orderIndex " + order.code(), Position.class
        ).getResultList();
    }

    /**
     * Finds all positions in ascending order of {@code orderIndex}.
     *
     * @param entityManager the entity manager used to execute the query
     * @return positions sorted in ascending order
     */
    public List<Position> findAllOrdered(EntityManager entityManager) {
        return entityManager.createQuery(
                "SELECT p FROM Position p ORDER BY p.orderIndex ", Position.class
        ).getResultList();
    }

   /* @Transactional
    public void moveUp(Position pos, EntityManager entityManager) {
        List<Position> positions = loadAll(entityManager);
        int index = positions.indexOf(pos);

        if (index > 0) {
            Position above = positions.get(index - 1);

            Long temp = pos.getOrderIndex();
            pos.setOrderIndex(above.getOrderIndex());
            above.setOrderIndex(temp);

            entityManager.merge(pos);
            entityManager.merge(above);

            Collections.swap(positions, index, index - 1);
        }
    }

    @Transactional
    public void moveDown(Position pos, EntityManager entityManager) {
        List<Position> positions = loadAll(entityManager);
        int index = positions.indexOf(pos);

        if (index < positions.size() - 1) {
            Position below = positions.get(index + 1);

            Long temp = pos.getOrderIndex();
            pos.setOrderIndex(below.getOrderIndex());
            below.setOrderIndex(temp);

            entityManager.merge(pos);
            entityManager.merge(below);

            Collections.swap(positions, index, index + 1);
        }
    }*/
}
