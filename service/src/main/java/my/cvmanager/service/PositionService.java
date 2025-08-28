package my.cvmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import my.cvmanager.domain.Position;
import my.cvmanager.repositories.PositionDao;

import java.util.*;
import java.util.logging.Logger;

/**
 * Service class responsible for managing positions.
 * <p>
 * This class provides methods for CRUD (Create, Read, Update, Delete) operations on positions.
 *
 * @author marco.braun
 */
@ApplicationScoped
public class PositionService {
    private final Logger logger = Logger.getLogger(PositionService.class.getCanonicalName());

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

    /**
     * move position down
     *
     * @param pos
     */
    @Transactional
    public void moveDown(Position pos) {
        List<Position> positions = findAll();
        int index = positions.indexOf(pos);
        if (index >= 0 && index < positions.size() - 1) {
            // Position tauschen
            Collections.swap(positions, index, index + 1);

            // orderIndex anpassen
            reindexInOrder(positions);

            logger.info("Moved position down: " + pos.getTitle());
        }
    }

    /**
     * move position up
     *
     * @param pos
     */
    @Transactional
    public void moveUp(Position pos) {
        List<Position> positions = findAll();
        int index = positions.indexOf(pos);
        if (index > 0) {
            // Position tauschen
            Collections.swap(positions, index, index - 1);

            // orderIndex anpassen
            reindexInOrder(positions);

            logger.info("Moved position up: " + pos.getTitle());
        }
    }

    /**
     * Hilfsmethode: orderIndex sauber neu setzen
     */
    @Transactional
    private void updateOrderIndices() {
        List<Position> positions = findAll();
        reindexInOrder(positions);
    }

    /**
     * Vergibt fortlaufende orderIndex (0..n) für übergebene Liste, in Listen-Reihenfolge.
     */
    @Transactional
    public void reindexInOrder(List<Position> ordered) {
        long idx = 0L;

        for (Position p : ordered) {
            p.setOrderIndex(idx++);
            update(p);
        }
    }

    /**
     * Get the next available orderIndex.
     *
     * @return max(orderIndex) + 1 or 0 if table is empty
     */
    @Transactional
    private Integer getNextOrderIndex() {
        Integer maxIndex = em.createQuery("SELECT MAX(p.orderIndex) FROM Position p", Integer.class)
                .getSingleResult();
        return (maxIndex == null) ? 0 : maxIndex + 1;
    }

    /**
     * Imports positions from CSV and assigns orderIndex based on startDate.
     * Newest position gets the lowest orderIndex (0), oldest gets the highest.
     */
    @Transactional
    public void importFromCsv(List<Position> importedPositions) {

        // 1. Sortieren: neueste zuerst
        importedPositions.sort(Comparator.comparing(Position::getStartDate).reversed());

        // 2. orderIndex vergeben
        for (int i = 0; i < importedPositions.size(); i++) {
            importedPositions.get(i).setOrderIndex((long) i);
            dao.persist(importedPositions.get(i), em);
        }
    }

    /**
     * Import new positions from CSV and merge with existing ones.
     * After merge, all positions are sorted by startDate (desc) and reindexed.
     */
    @Transactional
    public void importFromCsvOrdered(List<Position> importedPositions) {
        // Lade bestehende Positionen
        List<Position> allPositions = new ArrayList<>(dao.loadAll(em));

        // Neue Positionen hinzufügen
        allPositions.addAll(importedPositions);

        // Sortieren: jüngste zuerst (absteigend nach startDate)
        allPositions.sort(Comparator.comparing(Position::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())));

        // orderIndex neu vergeben (0 = erste Position = jüngste)
        Long index = 0L;
        for (Position pos : allPositions) {
            pos.setOrderIndex(index++);
            em.merge(pos); // merge, damit auch bestehende Positionen aktualisiert werden
        }
    }

    @Transactional
    public List<Position> findAllOrdered() {
        return dao.findAllOrdered(em);
    }

    @Transactional
    public List<Position> findAllOrderedUp() {
        return dao.findAllOrderedUp(em);
    }

    @Transactional
    public List<Position> findAllOrderedDown() {
        return dao.findAllOrderedDown(em);
    }
}