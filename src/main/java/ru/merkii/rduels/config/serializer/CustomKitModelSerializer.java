package ru.merkii.rduels.config.serializer;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;

import java.lang.reflect.Type;

public class CustomKitModelSerializer implements TypeSerializer<CustomKitModel> {

    @Override
    public CustomKitModel deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String displayName = node.node("display-name").getString();
        if (displayName == null || displayName.isBlank()) {
            throw new SerializationException("Отсутствует 'display-name' в конфиге кита");
        }

        String permission = node.node("permission").getString();
        boolean invisible = node.node("invisible").getBoolean(false);

        return new CustomKitModel(displayName, permission, invisible);
    }

    @Override
    public void serialize(Type type, CustomKitModel value, ConfigurationNode node) throws SerializationException {
        // Implement if saving is required
    }

}