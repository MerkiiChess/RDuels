package ru.merkii.rduels.gui.internal.paged;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PageResolverRegistry {

    Map<String, PageResolver<?>> pageResolvers = new HashMap<>();

    public Optional<PageResolver<?>> pageResolver(String id) {
        return Optional.ofNullable(pageResolvers.get(id));
    }

    public void register(String id, PageResolver<?> resolver) {
        requireNonNull(id);
        requireNonNull(resolver);
        if (pageResolvers.containsKey(id))
            throw new IllegalArgumentException("Cannot override page resolver with id '" + id + "'");
        pageResolvers.put(id, resolver);
    }

}
