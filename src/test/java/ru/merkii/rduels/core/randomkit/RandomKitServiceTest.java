package ru.merkii.rduels.core.randomkit;

import org.junit.jupiter.api.Test;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.settings.KitConfiguration;
import ru.merkii.rduels.core.randomkit.config.RandomKitConfiguration;
import ru.merkii.rduels.model.KitModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the include/exclude filtering and empty-pool safety of the random-kit pool.
 * Kits are built with a null display material so no Bukkit registry is touched.
 */
class RandomKitServiceTest {

    private KitModel kit(String name) {
        return new KitModel(name, 0, new ArrayList<>(), null, new LinkedHashMap<>(), false, new ArrayList<>());
    }

    private KitConfiguration kitConfig(KitModel... kits) {
        Map<KitModel, ItemBuilder> map = new LinkedHashMap<>();
        for (KitModel kit : kits) {
            map.put(kit, null);
        }
        return new KitConfiguration() {
            @Override public Map<KitModel, ItemBuilder> kits() { return map; }
            @Override public void kits(Map<KitModel, ItemBuilder> list) { }
        };
    }

    private RandomKitConfiguration randomConfig(List<String> include, List<String> exclude) {
        return new RandomKitConfiguration() {
            @Override public boolean enabled() { return true; }
            @Override public List<String> include() { return include; }
            @Override public List<String> exclude() { return exclude; }
        };
    }

    @Test
    void picksFromWholePoolWhenNoFilters() {
        RandomKitService service = new RandomKitService(kitConfig(kit("Alpha")), randomConfig(List.of(), List.of()));
        KitModel picked = service.pickRandom();
        assertNotNull(picked);
        assertEquals("Alpha", picked.getDisplayName());
    }

    @Test
    void excludeRemovesKit() {
        RandomKitService service = new RandomKitService(
                kitConfig(kit("Alpha"), kit("Beta")),
                randomConfig(List.of(), List.of("beta")));
        for (int i = 0; i < 20; i++) {
            assertEquals("Alpha", service.pickRandom().getDisplayName());
        }
    }

    @Test
    void includeRestrictsPool() {
        RandomKitService service = new RandomKitService(
                kitConfig(kit("Alpha"), kit("Beta")),
                randomConfig(List.of("beta"), List.of()));
        for (int i = 0; i < 20; i++) {
            assertEquals("Beta", service.pickRandom().getDisplayName());
        }
    }

    @Test
    void returnsNullWhenPoolEmpty() {
        RandomKitService service = new RandomKitService(
                kitConfig(kit("Alpha")),
                randomConfig(List.of(), List.of("alpha")));
        assertNull(service.pickRandom());
    }

    @Test
    void filtersAreCaseInsensitive() {
        RandomKitService service = new RandomKitService(
                kitConfig(kit("Alpha"), kit("Beta")),
                randomConfig(List.of(), List.of("BETA")));
        assertTrue(service.pickRandom().getDisplayName().equals("Alpha"));
    }
}
