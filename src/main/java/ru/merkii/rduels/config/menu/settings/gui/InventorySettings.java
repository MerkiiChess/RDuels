package ru.merkii.rduels.config.menu.settings.gui;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import com.bivashy.configurate.objectmapping.meta.Required;
import com.bivashy.configurate.objectmapping.meta.Transient;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

@ConfigInterface
public interface InventorySettings {

    List<String> pageItems();

    String pageResolver();

    @Required
    List<String> structure();

    @Required
    Map<Character, String> mapping();

    @Required
    Map<String, InventoryItem> items();

    Optional<AnimationSettings> animation();

    @Transient
    default char pageItemChar(String value) {
        if (value == null || value.length() != 1) {
            throw new IllegalStateException("page-item должен содержать ровно 1 символ, сейчас: " + value);
        }
        return value.charAt(0);
    }

    @Transient
    default List<Character> pageItemChars() {
        return pageItems().stream().map(this::pageItemChar).toList();
    }

    @Transient
    default Optional<InventoryItem> pageIngredientItem(String value) {
        return ingredient(pageItemChar(value));
    }

    @Transient
    default List<InventoryItem> pageIngredientItems() {
        return pageItemChars().stream()
                .map(character -> pageIngredientItem(character.toString()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Transient
    default Map<Character, InventoryItem> mappingIngredients() {
        return mapping().entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), findOrThrow(entry.getValue())))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    @Transient
    default InventoryItem findOrThrow(String key) {
        return item(key).orElseThrow(() -> new IllegalStateException("Item '" + key + "' not found"));
    }

    @Transient
    default Optional<InventoryItem> item(String key) {
        return Optional.ofNullable(items().get(key));
    }

    @Transient
    default Optional<InventoryItem> ingredient(Character mappingKey) {
        Optional<String> keyOptional = Optional.ofNullable(mapping().get(mappingKey));
        return keyOptional.flatMap(this::item);
    }

    @Transient
    default List<String> structureLines() {
        return structure();
    }

}