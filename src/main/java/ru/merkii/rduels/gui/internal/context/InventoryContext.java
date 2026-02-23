package ru.merkii.rduels.gui.internal.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface InventoryContext {

    static InventoryContext empty() {
        return create(new HashMap<>());
    }

    static InventoryContext create(Map<String, Object> raw) {
        return () -> raw;
    }

    default <T> Optional<T> get(String key) {
        Object object = raw().get(key);
        return Optional.ofNullable((T) object);
    }

    default <T> T require(String key) {
        Optional<T> value = get(key);
        return value.orElseThrow(() -> new IllegalStateException("Cannot find key '" + key + "'"));
    }

    default void extend(String key, Object value) {
        if (raw().containsKey(key))
            return;
        raw().put(key, value);
    }

    default InventoryContext copy() {
        return InventoryContext.create(new HashMap<>(raw()));
    }

    default void overrideOrCreate(String key, Object value) {
        raw().put(key, value);
    }

    Map<String, Object> raw();

}
