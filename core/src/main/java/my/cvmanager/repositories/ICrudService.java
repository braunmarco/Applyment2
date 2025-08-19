package my.cvmanager.repositories;

import java.util.List;

public interface ICrudService<T> {
    void persist(T entity);

    T find(Class<T> clazz, Long id);

    T update(T entity);

    void delete(T entity);

    List<T> loadAll(final Class<T> clazz, final String query);
}