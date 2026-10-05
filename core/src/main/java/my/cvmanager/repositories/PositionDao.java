package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import my.cvmanager.domain.Position;

import java.util.List;
import java.util.logging.Logger;

public class PositionDao extends BaseDao<Position> {
    private final Logger logger = Logger.getLogger(PositionDao.class.getCanonicalName());

    public PositionDao() {
        super(Position.class);
    }

    public enum Order {
        UP("ASC"),
        DOWN("DESC");

        public String order;

        Order(String order) {
            this.order = order;
        }

        public String code() {
            return order;
        }
    }

    public List<Position> findAllOrderedUp(EntityManager entityManager) {
        return findAllOrdered(entityManager, Order.UP);
    }

    public List<Position> findAllOrderedDown(EntityManager entityManager) {
        return findAllOrdered(entityManager, Order.DOWN);
    }

    public List<Position> findAllOrdered(EntityManager entityManager, Order order) {
        return entityManager.createQuery(
                "SELECT p FROM Position p ORDER BY p.orderIndex " + order.code(), Position.class
        ).getResultList();
    }

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
