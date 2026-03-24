package ru.merkii.rduels.core.customkit.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public class CustomKitEnchantCategory {

    String nameEnchant;
    int lvl;
    List<String> materialsEnchanted;

}
