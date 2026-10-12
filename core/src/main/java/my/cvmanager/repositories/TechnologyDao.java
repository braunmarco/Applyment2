package my.cvmanager.repositories;

import my.cvmanager.domain.Technology;

/**
 * Data Access Object (DAO) for {@link Technology} entities.
 *
 * <p>Inherits standard CRUD operations from {@link BaseDao}.</p>
 *
 * @see BaseDao
 * @see Technology
 */
public class TechnologyDao extends BaseDao<Technology> {

    /**
     * Creates a DAO for {@link Technology} entities.
     */
    public TechnologyDao() {
        super(Technology.class);
    }
}
