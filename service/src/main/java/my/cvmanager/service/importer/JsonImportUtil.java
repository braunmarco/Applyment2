package my.cvmanager.service.importer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * Very small JSON import helper.
 */
public final class JsonImportUtil {
    private static final ObjectMapper OM = new ObjectMapper();


    private JsonImportUtil() {
    }


    /**
     * Reads either a single object or an array from JSON into a list of T.
     */
    public static <T> List<T> readMany(String json, Class<T> type) {
        try {
            JsonNode root = OM.readTree(json);
            List<T> out = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode n : root) out.add(OM.convertValue(n, type));
            } else if (root.isObject()) {
                out.add(OM.convertValue(root, type));
            }
            return out;
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON", e);
        }
    }


    /**
     * Upserts a list of items: if finder finds an existing entity, merge it; otherwise persist.
     */
    /*public static <T> void upsertAll(
            List<T> items,
            EntityManager em,
            Function<T, Optional<T>> finder,
            BiConsumer<T, T> merger
    ) {
        for (T incoming : items) {
            Optional<T> existing = finder.apply(incoming);
            if (existing.isPresent()) {
                T managed = existing.get();
                merger.accept(managed, incoming); // copy fields from incoming -> managed
                em.merge(managed);
            } else {
                em.persist(incoming);
            }
        }
    }*/

    /**
     * Upserts a list of items: if finder finds an existing entity, merge it; otherwise persist.
     */
    public static <T> void upsertAll(
            List<T> items,
            EntityManager em,
            Function<T, T> finder,
            BiConsumer<T, T> merger
    ) {
        for (T incoming : items) {
            Optional<T> existing = Optional.ofNullable(finder.apply(incoming));
            if (existing.isPresent()) {
                T managed = existing.get();
                merger.accept(managed, incoming); // copy fields from incoming -> managed
                em.merge(managed);
            } else {
                em.persist(incoming);
            }
        }
    }
}