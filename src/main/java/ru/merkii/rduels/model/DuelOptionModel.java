package ru.merkii.rduels.model;

import ru.merkii.rduels.builder.ItemBuilder;

public record DuelOptionModel(String name, String type, ItemBuilder itemBuilder, Object model) {
}
