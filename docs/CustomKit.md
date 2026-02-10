📦 CustomKitAPI - Полное руководство
📖 Обзор

CustomKitAPI предоставляет интерфейс для работы с кастомными наборами игроков. В отличие от серверных наборов, кастомные наборы сохраняются индивидуально для каждого игрока, позволяя создавать персонализированные конфигурации снаряжения для дуэлей.
🚀 Быстрый старт
Получение экземпляра API
```java

import ru.merkii.rduels.api.Duel;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;

CustomKitAPI customKitAPI = Duel.getCustomKitAPI();
```
📊 Методы CustomKitAPI
Получение информации о наборах
getSelectedKitDisplayName(Player player)

Получает название выбранного набора игрока.
```java

/**
 * Получение отображаемого имени выбранного набора игрока
 * @param player - игрок
 * @return имя набора или "NULL" если набор не выбран
 */
String getSelectedKitDisplayName(Player player);
```
Пример использования:
```java

public class KitDisplay {
    public void showSelectedKit(Player player) {
        CustomKitAPI api = Duel.getCustomKitAPI();
        String kitName = api.getSelectedKitDisplayName(player);
        
        if (kitName.equals("NULL")) {
            player.sendMessage("Вы не выбрали набор!");
        } else {
            player.sendMessage("Ваш текущий набор: " + kitName);
        }
    }
}
```
getNameKitSlot(int slot)

Получает название набора по слоту в меню.
```java

/**
 * Получение имени набора по номеру слота в меню
 * @param slot - номер слота (0-53 для стандартного инвентаря)
 * @return имя набора или null если слот пустой
 */
String getNameKitSlot(int slot);
```
Пример использования:
```java

public class SlotManager {
    public String getKitNameAtSlot(int clickedSlot) {
        CustomKitAPI api = Duel.getCustomKitAPI();
        String kitName = api.getNameKitSlot(clickedSlot);
        
        if (kitName == null) {
            return "Пустой слот";
        }
        return "Слот " + clickedSlot + ": " + kitName;
    }
}
```
isSelectedKit(Player player, String kitName)

Проверяет, выбран ли указанный набор игроком.
```java

/**
 * Проверка, выбран ли набор игроком
 * @param player - игрок
 * @param kitName - название набора для проверки
 * @return true если набор выбран
 */
boolean isSelectedKit(Player player, String kitName);
```
Пример использования:
```java

public class KitValidator {
    public boolean validateKitSelection(Player player, String kitToCheck) {
        CustomKitAPI api = Duel.getCustomKitAPI();
        return api.isSelectedKit(player, kitToCheck);
    }
}
```
Управление наборами
setKit(Player player, String name)

Устанавливает выбранный набор для игрока.
```java

/**
 * Установка выбранного набора для игрока
 * @param player - игрок
 * @param name - название набора
 */
void setKit(Player player, String name);
```
Пример использования:
```java

public class KitSelector {
    public void selectKitForDuel(Player player, String kitName) {
        CustomKitAPI api = Duel.getCustomKitAPI();
        
        // Проверяем существование набора
        if (kitExists(player, kitName)) {
            api.setKit(player, kitName);
            player.sendMessage("Набор '" + kitName + "' выбран!");
        } else {
            player.sendMessage("Набор '" + kitName + "' не найден!");
        }
    }
    
    private boolean kitExists(Player player, String kitName) {
        return kitName != "NULL";
    }
}
```
getItemsFromKit(Player player, String kitName)

Получает список предметов из указанного набора игрока.
```java

/**
 * Получение предметов из набора игрока
 * @param player - владелец набора
 * @param kitName - название набора
 * @return список ItemStack из набора
 */
List<ItemStack> getItemsFromKit(Player player, String kitName);

Пример использования:
java

public class KitPreview {
    public void previewKitItems(Player player, String kitName) {
        CustomKitAPI api = Duel.getCustomKitAPI();
        List<ItemStack> items = api.getItemsFromKit(player, kitName);
        
        player.sendMessage("Набор '" + kitName + "' содержит " + items.size() + " предметов:");
        for (ItemStack item : items) {
            if (item != null && !item.getType().isAir()) {
                player.sendMessage("  - " + item.getType() + " x" + item.getAmount());
            }
        }
    }
}
```
getKitModel(Player player)

Получает модель KitModel выбранного набора игрока.
```java

/**
 * Получение модели набора игрока
 * @param player - игрок
 * @return KitModel выбранного набора
 */
KitModel getKitModel(Player player);
```
Пример использования:
```java

public class KitModelManager {
    public void useSelectedKit(Player player) {
        CustomKitAPI api = Duel.getCustomKitAPI();
        KitModel kitModel = api.getKitModel(player);
        
        if (kitModel != null) {
            // Используем модель набора для выдачи предметов
            kitModel.giveItemPlayers(player);
            player.sendMessage("Выдан набор: " + kitModel.getDisplayName());
        }
    }
}
```
