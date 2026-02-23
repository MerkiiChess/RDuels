package ru.merkii.rduels.gui.internal.extractor;

import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;
import ru.merkii.rduels.gui.internal.context.InventoryContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.spongepowered.configurate.NodePath.path;
import static org.spongepowered.configurate.transformation.ConfigurationTransformation.WILDCARD_OBJECT;

public class ValueExtractorRegistry {

    private final List<ValueExtractor> valueExtractors = new ArrayList<>();

    public void register(ValueExtractor extractor) {
        requireNonNull(extractor);
        valueExtractors.add(extractor);
    }

    public String extract(InventoryContext context, String text, Object model) {
        for (ValueExtractor valueExtractor : valueExtractors) {
            String result = valueExtractor.extract(context, text, model);
            if (result == null)
                continue;
            text = result;
        }
        return text;
    }

    public ConfigurationTransformation asTransformer(InventoryContext context, Object model) {
        TransformAction action = (path, value) -> {
            String textValue = value.getString();
            if (textValue != null)
                value.set(String.class, extract(context, textValue, model));
            if (value.isList()) {
                List<String> originalValue = value.getList(String.class);
                if (originalValue != null) {
                    List<String> mappedValue = originalValue.stream().map(text -> extract(context, text, model)).collect(Collectors.toList());
                    value.set(mappedValue);
                }
            }
            return null;
        };
        return ConfigurationTransformation.builder()
                .addAction(path(WILDCARD_OBJECT), action)
                .addAction(path(WILDCARD_OBJECT, WILDCARD_OBJECT), action)
                .build();
    }

    public Collection<ValueExtractor> all() {
        return Collections.unmodifiableCollection(valueExtractors);
    }

}
