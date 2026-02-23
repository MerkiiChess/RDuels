package ru.merkii.rduels.core.customkit.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CustomKitEnchantCategory {

    private String nameEnchant;
    private int lvl;
    private int slot;
    private List<String> materialsEnchanted;

}
