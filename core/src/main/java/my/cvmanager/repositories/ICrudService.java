package my.cvmanager.repositories;

import java.util.List;
import java.util.Map;

public interface ICrudService<T> {
    void persist(T entity);

    T find(Class<T> clazz, Long id);

    T update(T entity);

    T findOne(Class<T> clazz, String query);

    T findOne(Class<T> clazz, String attr, Object value);

    T findOne(Class<T> clazz, Map<String, Object> params);

    void delete(T entity);

    List<T> loadAll(final Class<T> clazz, final String query);
}