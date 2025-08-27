package my.cvmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import my.cvmanager.domain.Technology;
import my.cvmanager.repositories.TechnologyDao;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing technologies.
 * <p>
 * This class provides methods for CRUD (Create, Read, Update, Delete) operations on technologies.
 *
 * @author marco.braun
 */
@ApplicationScoped
public class TechnologyService {

    /**
     * Entity manager instance for database operations.
     */
    @PersistenceContext(unitName = "cvmanagerPU")
    private EntityManager em;

    /**
     * Data access object for technologies.
     */
    private final TechnologyDao dao = new TechnologyDao();

    /**
     * Saves a new technology to the database.
     *
     * @param tech the technology to save
     */
    @Transactional
    public void save(Technology tech) {
        dao.persist(tech, em);
    }

    /**
     * Updates an existing technology in the database.
     *
     * @param tech the technology to update
     * @return the updated technology
     */
    @Transactional
    public Technology update(Technology tech) {
        return dao.update(tech, em);
    }

    /**
     * Deletes a technology from the database by its ID.
     *
     * @param id the ID of the technology to delete
     */
    @Transactional
    public void delete(Long id) {
        Optional<Technology> tech = dao.find(id, em);

        tech.ifPresent(technology -> em.remove(technology));
    }

    /**
     * Retrieves all technologies from the database.
     *
     * @return a list of all technologies
     */
    public List<Technology> findAll() {
        return dao.loadAll(em);
    }
}