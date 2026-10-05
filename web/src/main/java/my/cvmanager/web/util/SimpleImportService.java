package my.cvmanager.web.util;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import my.cvmanager.domain.Position;
import my.cvmanager.domain.Technology;
import my.cvmanager.service.PositionService;
import my.cvmanager.service.TechnologyService;
import my.cvmanager.service.importer.JsonImportUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;


@ApplicationScoped
public class SimpleImportService {
    @PersistenceContext(unitName = "cvmanagerPU")
    private EntityManager em;
    @Inject
    PositionService positionDao;
    @Inject
    TechnologyService technologyDao;
    @Inject
    private PositionService positionService;

    /**
     * Import positions for a given userProfile using a natural key (userProfileId + title + company + startDate).
     */
    @Transactional
    public void importPositions(String json, Long userProfileId) {
        List<Position> items = JsonImportUtil.readMany(json, Position.class);
        // Ensure each new entity is associated with the given user profile
        /*items.forEach(p -> {
            if (p.getUserProfile() == null || p.getUserProfile().getId() == null) {
                UserProfile up = new UserProfile();
                up.setId(userProfileId);
                p.setUserProfile(up);
            }
        });*/


        Function<Position, Position> positionFinder = position -> {
            Map<String, Object> attributesMap = new HashMap<>();
            attributesMap.put("title", position.getTitle());
            attributesMap.put("company", position.getCompany());

            return positionDao.findOne(attributesMap);
        };

        BiConsumer<Position, Position> positionMerger = (existing, incoming) -> {
            existing.setTitle(incoming.getTitle());
            existing.setCompany(incoming.getCompany());
            existing.setLocation(incoming.getLocation());
            existing.setStartDate(incoming.getStartDate());
            existing.setEndDate(incoming.getEndDate());
            existing.setDescription(incoming.getDescription());

            // Optional: replace technologies if you import them together
            if (incoming.getTechnologies() != null) {
                existing.getTechnologies().clear();
                incoming.getTechnologies().forEach(t -> {
                    t.setPosition(existing);
                    existing.getTechnologies().add(t);
                });
            }
        };


        JsonImportUtil.upsertAll(
                items,
                em,
                positionFinder,
                positionMerger
        );
    }

    Function<Technology, Technology> technologyFinder = technology -> {
        Map<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("name", technology.getName());

        return technologyDao.findOne(attributesMap);
    };

    BiConsumer<Technology, Technology> technologyMerger = (existing, incoming) -> {
        existing.setName(incoming.getName());
        existing.setLevel(incoming.getLevel());
    };

    /**
     * Import technologies by name (simple example unchanged).
     */
    @Transactional
    public void importTechnologies(String json) {
        List<Technology> items = JsonImportUtil.readMany(json, Technology.class);
        JsonImportUtil.upsertAll(
                items,
                em,
                technologyFinder,
                technologyMerger
        );
    }
}