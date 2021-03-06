package no.hvl.dat110.rest.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(int id);

    List<T> getAll();

    void save(T t);

    void update(T t, Object[] params);

    void delete(T t);
}