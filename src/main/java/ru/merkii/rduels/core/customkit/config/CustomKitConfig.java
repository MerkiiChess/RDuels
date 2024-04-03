package ru.merkii.rduels.core.customkit.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import ru.merkii.rduels.builder.ItemBuilder;
import ru.merkii.rduels.config.settings.Config;
import ru.merkii.rduels.core.customkit.category.CustomKitCategory;
import ru.merkii.rduels.core.customkit.category.CustomKitCategoryEnchantItemType;
import ru.merkii.rduels.core.customkit.category.CustomKitEnchantCategory;
import ru.merkii.rduels.core.customkit.model.CustomKitModel;

import java.util.List;
import java.util.Map;

@Getter
public class CustomKitConfig extends Config {

    private CreateMenu createMenu = new CreateMenu();
    private EditMenu editMenu = new EditMenu();
    private CategoriesMenu categoriesMenu = new CategoriesMenu();
    private int size = 54;
    private String title = "Кастомные киты";
    private String titleEdit = "Редактирование кита: (kit)";
    private String titleCreate = "Создание кастомного кита";

    @Getter
    public static class CreateMenu {
        private ItemBuilder editItem = ItemBuilder.builder().setMaterial(Material.WRITTEN_BOOK).setDisplayName("Редактирование кита: (kit)").setLore(fastList("Здесь вы можете отредактировать свой кит"));
        private ItemBuilder noPermissionItem = ItemBuilder.builder().setMaterial(Material.RED_GLAZED_TERRACOTTA).setDisplayName("У вас не достаточно прав чтобы использовать этот слот").setLore(fastList("Чтобы использовать данный слот, ", "вы можете приобрести донат на нашем сайте :)"));
        private ItemBuilder notSelected = ItemBuilder.builder().setMaterial(Material.GRAY_GLAZED_TERRACOTTA).setDisplayName("Нажми сюда чтобы выбрать данные кит");
        private ItemBuilder selected = ItemBuilder.builder().setMaterial(Material.GREEN_GLAZED_TERRACOTTA).setDisplayName("Сейчас активен именно этот кит");
        private List<CustomKitModel> kits = fastList(CustomKitModel.create("Кит №1", 20, null, false), CustomKitModel.create("Кит №2", 22, "test.perm1", false), CustomKitModel.create("Кит №3", 24, "test.perm2", true));

    }

    @Getter
    public static class EditMenu {
        private int size = 54;
        private ItemBuilder exit = ItemBuilder.builder().setMaterial(Material.ARROW).setDisplayName("Назад").setLore(fastList("Вернуться к китам"));
        private ItemBuilder noSlotMainLine = ItemBuilder.builder().setMaterial(Material.CYAN_GLAZED_TERRACOTTA).setDisplayName("Нажми чтоб выбрать предмет").addItemFlags(ItemFlag.values());
        private ItemBuilder noSlotOtherLines = ItemBuilder.builder().setMaterial(Material.GRAY_GLAZED_TERRACOTTA).setDisplayName("Нажми чтоб выбрать предмет").addItemFlags(ItemFlag.values());
        private ItemBuilder noSlotHelmet = ItemBuilder.builder().setMaterial(Material.YELLOW_GLAZED_TERRACOTTA).setDisplayName("Нажми чтоб выбрать предмет").addItemFlags(ItemFlag.values());
        private ItemBuilder noSlotChestplate = ItemBuilder.builder().setMaterial(Material.YELLOW_GLAZED_TERRACOTTA).setDisplayName("Нажми чтоб выбрать предмет").addItemFlags(ItemFlag.values());
        private ItemBuilder noSlotLeggings = ItemBuilder.builder().setMaterial(Material.YELLOW_GLAZED_TERRACOTTA).setDisplayName("Нажми чтоб выбрать предмет").addItemFlags(ItemFlag.values());
        private ItemBuilder noSlotBoots = ItemBuilder.builder().setMaterial(Material.YELLOW_GLAZED_TERRACOTTA).setDisplayName("Нажми чтоб выбрать предмет").addItemFlags(ItemFlag.values());
        private ItemBuilder noSlotOffHand = ItemBuilder.builder().setMaterial(Material.LIGHT_BLUE_GLAZED_TERRACOTTA).setDisplayName("Нажми чтоб выбрать предмет").addItemFlags(ItemFlag.values());

    }

    @Getter
    public static class CategoriesMenu {
        private int size = 54;
        private ItemBuilder exit = ItemBuilder.builder().setMaterial(Material.ARROW).setDisplayName("Назад").setLore(fastList("Вернуться к настройке кита"));
        private Map<ItemBuilder, Integer> itemStacks = fastMap(fastList(ItemBuilder.builder().setMaterial("NULL").setDisplayName("1 штука").setSlot(13), ItemBuilder.builder().setMaterial("NULL").setDisplayName("8 штук").setAmount(8).setSlot(14), ItemBuilder.builder().setMaterial("NULL").setDisplayName("16 штук").setAmount(16).setSlot(15), ItemBuilder.builder().setMaterial("NULL").setDisplayName("32 штуки").setAmount(32).setSlot(16), ItemBuilder.builder().setMaterial("NULL").setDisplayName("64 штуки").setAmount(64).setSlot(17)), fastList(1, 8, 16, 32, 64));
        private List<CustomKitCategory> categories = fastList(new CustomKitCategory(ItemBuilder.builder().setMaterial(Material.DIAMOND_SWORD).setDisplayName("Мечи").setLore(fastList("Здесь вы найдете мечи")).addItemFlags(ItemFlag.values()).setSlot(20), fastMap(fastList(1,2), fastList(Material.DIAMOND_SWORD, Material.GOLDEN_SWORD))), new CustomKitCategory(ItemBuilder.builder().setMaterial(Material.DIAMOND_CHESTPLATE).setDisplayName("Броня").addItemFlags(ItemFlag.values()).setSlot(21), fastMap(fastList(1, 2, 3, 4), fastList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS))));
        private List<CustomKitEnchantCategory> enchantCategories = fastList(CustomKitEnchantCategory.create("DAMAGE_ALL", 1, 0, CustomKitCategoryEnchantItemType.SWORD), CustomKitEnchantCategory.create("DAMAGE_ALL", 2, 1, CustomKitCategoryEnchantItemType.SWORD));
    }

}
