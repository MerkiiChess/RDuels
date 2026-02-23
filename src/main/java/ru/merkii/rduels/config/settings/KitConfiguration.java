package ru.merkii.rduels.config.settings;

import com.bivashy.configurate.objectmapping.ConfigInterface;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.model.KitModel;

import java.util.List;
import java.util.Map;

@ConfigInterface
public interface KitConfiguration {

    Map<KitModel, ItemBuilder> kits();
    void kits(Map<KitModel, ItemBuilder> list);

}
