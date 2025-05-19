package com.crozhere.service.cms.common;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemRepository<T> {

    private final Map<Long, T> store = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);
    private final IdSetter<T> idSetter;

    public InMemRepository(IdSetter<T> idSetter) {
        this.idSetter = idSetter;
    }

    public T save(T entity) {
        Long id = sequence.getAndIncrement();
        idSetter.setId(entity, id);
        store.put(id, entity);
        return entity;
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public T getById(Long id) {
        return findById(id).orElseThrow(
                        () -> new NoSuchElementException("Entity not found with ID: " + id));
    }

    public void update(Long id, T updatedEntity) {
        if (!store.containsKey(id)) {
            throw new NoSuchElementException("Cannot update. Entity not found with ID: " + id);
        }
        idSetter.setId(updatedEntity, id);
        store.put(id, updatedEntity);
    }

    public void deleteById(Long id) {
        if (!store.containsKey(id)) {
            throw new NoSuchElementException("Cannot delete. Entity not found with ID: " + id);
        }
        store.remove(id);
    }

    public Collection<T> findAll() {
        return store.values();
    }

    @FunctionalInterface
    public interface IdSetter<T> {
        void setId(T entity, Long id);
    }
}
