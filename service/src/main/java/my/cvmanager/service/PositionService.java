package my.cvmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import my.cvmanager.domain.Position;
import my.cvmanager.repositories.PositionDao;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing positions.
 * <p>
 * This class provides methods for CRUD (Create, Read, Update, Delete) operations on positions.
 *
 * @author marco.braun
 */
@ApplicationScoped
public class PositionService {

    /**
     * Entity manager instance for database operations.
     */
    @PersistenceContext(unitName = "cvmanagerPU")
    private EntityManager em;

    /**
     * Data access object for positions.
     */
    private final PositionDao dao = new PositionDao();

    /**
     * Saves a new position to the database.
     *
     * @param pos the position to save
     */
    @Transactional
    public void save(Position pos) {
        dao.persist(pos, em);
    }

    /**
     * Updates an existing position in the database.
     *
     * @param pos the position to update
     * @return the updated position
     */
    @Transactional
    public Position update(Position pos) {
        return dao.update(pos, em);
    }

    /**
     * Deletes a position from the database by its ID.
     *
     * @param id the ID of the position to delete
     */
    @Transactional
    public void delete(Long id) {
        Optional<Position> pos = dao.find(id, em);

        pos.ifPresent(position -> em.remove(position));
    }

    /**
     * Retrieves all positions from the database.
     *
     * @return a list of all positions
     */
    public List<Position> findAll() {
        return dao.loadAll(em);
    }
}