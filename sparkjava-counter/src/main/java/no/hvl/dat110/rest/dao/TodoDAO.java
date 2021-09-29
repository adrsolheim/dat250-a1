package no.hvl.dat110.rest.dao;

import no.hvl.dat110.rest.model.Todo;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class TodoDAO implements Dao<Todo> {

    private EntityManager entityManager;

    public TodoDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Todo> get(int id) {
        return Optional.ofNullable(entityManager.find(Todo.class, id));
    }

    @Override
    public List<Todo> getAll() {
        Query query = entityManager.createQuery("SELECT t FROM Todo t");
        return query.getResultList();
    }

    @Override
    public void save(Todo todo) {
        executeInsideTransaction(entityManager -> entityManager.persist(todo));
    }

    @Override
    public void update(Todo todo, Object[] params) {
        todo.setSummary((String) Objects.requireNonNull(params[0], "Summary cannot be null"));
        todo.setDescription((String) Objects.requireNonNull(params[1], "Description cannot be null"));
        executeInsideTransaction(entityManager -> entityManager.merge(todo));
    }

    @Override
    public void delete(Todo todo) {
        executeInsideTransaction(entityManager -> entityManager.remove(todo));
    }

    private void executeInsideTransaction(Consumer<EntityManager> action) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            action.accept(entityManager);
            tx.commit();
        }
        catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }
}