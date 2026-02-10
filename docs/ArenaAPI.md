🏟️ ArenaAPI - Полное руководство
📖 Обзор

ArenaAPI предоставляет интерфейс для управления аренами в системе дуэлей. Арены — это специально подготовленные локации для проведения боёв, которые могут быть настроены под различные форматы сражений (1v1, 2v2, FFA).
🚀 Быстрый старт
Получение экземпляра API
```java

import ru.merkii.rduels.api.Duel;
import ru.merkii.rduels.core.arena.api.ArenaAPI;

ArenaAPI arenaAPI = Duel.getArenaAPI();
```
📊 Методы ArenaAPI
Поиск и получение арен
getArenaFromName(String name)

Получает арену по её внутреннему имени.
```java

/**
 * Получение арены по имени
 * @param name - внутреннее имя арены (arenaName)
 * @return ArenaModel или null если не найдена
 */
@Nullable
ArenaModel getArenaFromName(String name);
```
Пример использования:
```java

public class ArenaFinder {
    public ArenaModel findArenaByName(String arenaName) {
        ArenaAPI api = Duel.getArenaAPI();
        ArenaModel arena = api.getArenaFromName(arenaName);
        
        if (arena == null) {
            throw new IllegalArgumentException("Арена '" + arenaName + "' не найдена!");
        }
        return arena;
    }
}
```
getArenaFromDisplayName(String displayName)

Получает арену по отображаемому имени.
```java

/**
 * Получение арены по отображаемому имени
 * @param displayName - отображаемое имя арены (может содержать цветовые коды)
 * @return ArenaModel или null если не найдена
 */
@Nullable
ArenaModel getArenaFromDisplayName(String displayName);
```
Пример использования:
```java

public class ArenaDisplayManager {
    public void showArenaInfo(Player player, String displayName) {
        ArenaAPI api = Duel.getArenaAPI();
        ArenaModel arena = api.getArenaFromDisplayName(displayName);
        
        if (arena != null) {
            player.sendMessage("Арена: " + arena.getDisplayName());
            player.sendMessage("Тип: " + (arena.isFfa() ? "FFA" : "Дуэль"));
            player.sendMessage("Схема: " + arena.getSchematic());
        }
    }
}
```
isContainsArena(String name)

Проверяет существование арены по имени.
```java

/**
 * Проверка существования арены по имени
 * @param name - имя арены для проверки
 * @return true если арена существует
 */
boolean isContainsArena(String name);
```
Пример использования:
```java

public class ArenaValidator {
    public boolean validateArenaName(String proposedName) {
        ArenaAPI api = Duel.getArenaAPI();
        
        if (api.isContainsArena(proposedName)) {
            throw new IllegalArgumentException("Арена с именем '" + proposedName + "' уже существует!");
        }
        
        // Дополнительные проверки
        if (proposedName.length() < 3) {
            throw new IllegalArgumentException("Имя арены должно содержать минимум 3 символа!");
        }
        
        return true;
    }
}
```
getArenasFromName(String name)

Получает список всех арен с указанным отображаемым именем.
```java

/**
 * Получение списка арен по отображаемому имени
 * @param name - отображаемое имя для поиска
 * @return список ArenaModel (может быть пустым)
 */
List<ArenaModel> getArenasFromName(String name);
```
Пример использования:
```java

public class ArenaCluster {
    public List<ArenaModel> getArenaCluster(String clusterName) {
        ArenaAPI api = Duel.getArenaAPI();
        List<ArenaModel> arenas = api.getArenasFromName(clusterName);
        
        // Фильтруем только свободные арены
        return arenas.stream()
            .filter(arena -> !api.isBusyArena(arena))
            .collect(Collectors.toList());
    }
}
```
Управление занятостью арен
addBusyArena(ArenaModel arenaModel)

Отмечает арену как занятую.
```java

/**
 * Добавление арены в список занятых
 * @param arenaModel - арена для отметки как занятой
 */
void addBusyArena(ArenaModel arenaModel);
```
Пример использования:
```java

public class ArenaReservation {
    public boolean reserveArenaForDuel(ArenaModel arena) {
        ArenaAPI api = Duel.getArenaAPI();
        
        if (api.isBusyArena(arena)) {
            return false; // Арена уже занята
        }
        
        api.addBusyArena(arena);
        return true;
    }
}

removeBusyArena(ArenaModel arenaModel)

Освобождает арену.
```java

/**
 * Удаление арены из списка занятых
 * @param arenaModel - арена для освобождения
 */
void removeBusyArena(ArenaModel arenaModel);
```
Пример использования:
```java

public class ArenaCleanup {
    public void cleanupAfterDuel(ArenaModel arena) {
        ArenaAPI api = Duel.getArenaAPI();
        
        // Освобождаем арену
        api.removeBusyArena(arena);
        
        // Восстанавливаем арену если нужно
        if (arena.isBreaking()) {
            api.restoreArena(arena);
        }
    }
}
```
isBusyArena(ArenaModel arenaModel)

Проверяет, занята ли арена.
```java

/**
 * Проверка занятости арены
 * @param arenaModel - арена для проверки
 * @return true если арена занята
 */
boolean isBusyArena(ArenaModel arenaModel);
```
Пример использования:
```java

public class ArenaScheduler {
    public ArenaModel findAvailableArena(List<ArenaModel> preferredArenas) {
        ArenaAPI api = Duel.getArenaAPI();
        
        for (ArenaModel arena : preferredArenas) {
            if (!api.isBusyArena(arena)) {
                return arena;
            }
        }
        
        // Ищем любую свободную арену
        return api.getArenasFromName("Default").stream()
            .filter(arena -> !api.isBusyArena(arena))
            .findFirst()
            .orElse(null);
    }
}
```
Восстановление и управление аренами
restoreArena(ArenaModel arenaModel)

Восстанавливает арену к исходному состоянию.
```java

/**
 * Восстановление арены
 * @param arenaModel - арена для восстановления
 */
void restoreArena(ArenaModel arenaModel);
```
Пример использования:
```java

public class ArenaMaintenance {
    public void performArenaReset(ArenaModel arena) {
        ArenaAPI api = Duel.getArenaAPI();
        
        if (api.isBusyArena(arena)) {
            throw new IllegalStateException("Нельзя восстановить занятую арену!");
        }
        
        api.restoreArena(arena);
        Bukkit.getLogger().info("Арена '" + arena.getDisplayName() + "' восстановлена.");
    }
}
```

getArenaFromKit(KitModel kitModel)

Находит свободную арену, поддерживающую указанный набор.
```java

/**
 * Поиск арены по поддерживаемому набору
 * @param kitModel - набор для поиска
 * @return Optional с ArenaModel если найдена подходящая арена
 */
Optional<ArenaModel> getArenaFromKit(KitModel kitModel);
```
Пример использования:
```java

public class KitArenaMatcher {
    public ArenaModel findArenaForKit(KitModel kit) {
        ArenaAPI api = Duel.getArenaAPI();
        
        return api.getArenaFromKit(kit)
            .orElseThrow(() -> new IllegalArgumentException(
                "Нет доступных арен для набора: " + kit.getDisplayName()
            ));
    }
}
```
🏗️ ArenaModel - Модель арены
Основные поля
```java

public class ArenaModel implements Cloneable {
    private final String arenaName;           // Внутреннее имя
    private final String displayName;         // Отображаемое имя
    private final Material material;          // Материал для иконки в меню
    
    // Позиции для дуэлей 1v1
    @Setter private EntityPosition onePosition;       // Позиция первого игрока
    @Setter private EntityPosition twoPosition;       // Позиция второго игрока
    
    // Позиция для зрителей
    @Setter private EntityPosition spectatorPosition;
    
    // Позиции для FFA (Free For All) - до 20 позиций
    @Setter private Map<Integer, EntityPosition> ffaPositions;
    
    // Позиция для загрузки схемы
    @Setter private EntityPosition schematicPosition;
    
    // Настройки
    private boolean customKits;               // Поддержка кастомных наборов
    private List<String> customKitsName;      // Список поддерживаемых наборов
    private final boolean ffa;                // Режим FFA
    private final boolean breaking;           // Автовосстановление
    private final String schematic;           // Имя файла схемы
    private final int radiusDeleteBlocks;     // Радиус очистки блоков
}
```

Создание арены
Использование конструктора
```java

public class ArenaFactory {
    public ArenaModel createBasicArena() {
        return ArenaModel.create(
            "arena1",                     // arenaName
            "§6Основная арена",          // displayName
            Material.DIAMOND_BLOCK,      // material
            new EntityPosition(...),     // onePosition
            new EntityPosition(...),     // twoPosition
            new EntityPosition(...)      // spectatorPosition
        );
    }
    
    public ArenaModel createFFAArena() {
        return ArenaModel.create(
            "ffa_arena",                 // arenaName
            "§cFFA Арена",              // displayName
            Material.EMERALD_BLOCK,     // material
            new EntityPosition(...),    // onePosition (не используется в FFA)
            new EntityPosition(...),    // twoPosition (не используется в FFA)
            new EntityPosition(...),    // spectatorPosition
            true,                       // ffa = true
            true,                       // breaking = true
            "arena_ffa.schem"           // schematic
        );
    }
}
```
Использование Builder
```java

public class ArenaBuilderExample {
    public ArenaModel buildCustomArena() {
        Map<Integer, EntityPosition> ffaPositions = new HashMap<>();
        ffaPositions.put(1, new EntityPosition(...)); // Позиция 1
        ffaPositions.put(2, new EntityPosition(...)); // Позиция 2
        ffaPositions.put(11, new EntityPosition(...)); // Позиция 11
        ffaPositions.put(12, new EntityPosition(...)); // Позиция 12
        
        return ArenaModel.builder()
            .arenaName("tournament_arena")
            .displayName("§5Турнирная арена")
            .material(Material.NETHERITE_BLOCK)
            .onePosition(new EntityPosition(...))
            .twoPosition(new EntityPosition(...))
            .spectatorPosition(new EntityPosition(...))
            .ffaPositions(ffaPositions)
            .schematicPosition(new EntityPosition(...))
            .customKits(true)
            .customKitsName(Arrays.asList("Tournament", "Premium"))
            .ffa(true)
            .breaking(true)
            .schematic("tournament.schem")
            .radiusDeleteBlocks(100)
            .build();
    }
}
```

Клонирование арены
```java

public class ArenaCloner {
    public ArenaModel createArenaVariant(ArenaModel original, String newName) {
        ArenaModel clone = original.clone();
        
        // Изменить поля нельзя (они final), но можно создать новую арену на основе оригинала
        return ArenaModel.builder()
            .arenaName(newName)
            .displayName(original.getDisplayName() + " (Копия)")
            .material(original.getMaterial())
            .onePosition(original.getOnePosition())
            .twoPosition(original.getTwoPosition())
            .spectatorPosition(original.getSpectatorPosition())
            .ffaPositions(new HashMap<>(original.getFfaPositions()))
            .schematicPosition(original.getSchematicPosition())
            .customKits(original.isCustomKits())
            .customKitsName(new ArrayList<>(original.getCustomKitsName()))
            .ffa(original.isFfa())
            .breaking(original.isBreaking())
            .schematic(original.getSchematic())
            .radiusDeleteBlocks(original.getRadiusDeleteBlocks())
            .build();
    }
}
```
Пример 3: Автоматическое создание арен
```java

public class ArenaGenerator {
    private final ArenaAPI arenaAPI;
    private final World world;
    
    public ArenaGenerator(World world) {
        this.arenaAPI = Duel.getArenaAPI();
        this.world = world;
    }
    
    public ArenaModel generateArenaFromTemplate(String templateName, Location center) {
        // Создаем базовую арену
        ArenaModel arena = ArenaModel.builder()
            .arenaName(generateArenaName(templateName))
            .displayName("§a" + templateName + " #" + System.currentTimeMillis())
            .material(getMaterialForTemplate(templateName))
            .onePosition(calculateSpawnPosition(center, 1))
            .twoPosition(calculateSpawnPosition(center, 2))
            .spectatorPosition(calculateSpectatorPosition(center))
            .ffa(templateName.contains("ffa"))
            .breaking(true)
            .schematic(templateName + ".schem")
            .radiusDeleteBlocks(calculateRadius(templateName))
            .build();
        
        // Настраиваем FFA позиции если нужно
        if (arena.isFfa()) {
            arena.setFfaPositions(generateFFAPositions(center));
        }
        
        // Устанавливаем позицию для схемы
        arena.setSchematicPosition(new EntityPosition(center));
        
        // Сохраняем схему
        saveSchematic(templateName, center);
        
        return arena;
    }
    
    private Map<Integer, EntityPosition> generateFFAPositions(Location center) {
        Map<Integer, EntityPosition> positions = new HashMap<>();
        
        // Генерируем позиции по кругу
        int playerCount = 20; // Максимум 20 игроков
        double radius = 10.0;
        
        for (int i = 1; i <= playerCount; i++) {
            double angle = 2 * Math.PI * i / playerCount;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            
            Location spawnLoc = new Location(center.getWorld(), x, center.getY(), z);
            spawnLoc.setYaw((float) Math.toDegrees(angle + Math.PI));
            
            positions.put(i, new EntityPosition(spawnLoc));
        }
        
        return positions;
    }
    
    private EntityPosition calculateSpawnPosition(Location center, int playerNum) {
        Location spawn = center.clone();
        
        if (playerNum == 1) {
            spawn.add(5, 0, 0); // Справа от центра
            spawn.setYaw(270); // Смотреть на запад
        } else {
            spawn.add(-5, 0, 0); // Слева от центра
            spawn.setYaw(90); // Смотреть на восток
        }
        
        return new EntityPosition(spawn);
    }
    
    private String generateArenaName(String baseName) {
        return baseName.toLowerCase() + "_" + 
               world.getName() + "_" + 
               System.currentTimeMillis();
    }
    
    private Material getMaterialForTemplate(String template) {
        switch (template.toLowerCase()) {
            case "forest": return Material.OAK_LOG;
            case "desert": return Material.SANDSTONE;
            case "nether": return Material.NETHERRACK;
            default: return Material.STONE;
        }
    }
    

}
```
Пример 1: Автоматическое создание арен
```java

public class ArenaGenerator {
    private final ArenaAPI arenaAPI;
    private final World world;
    
    public ArenaGenerator(World world) {
        this.arenaAPI = Duel.getArenaAPI();
        this.world = world;
    }
    
    public ArenaModel generateArenaFromTemplate(String templateName, Location center) {
        // Создаем базовую арену
        ArenaModel arena = ArenaModel.builder()
            .arenaName(generateArenaName(templateName))
            .displayName("§a" + templateName + " #" + System.currentTimeMillis())
            .material(getMaterialForTemplate(templateName))
            .onePosition(calculateSpawnPosition(center, 1))
            .twoPosition(calculateSpawnPosition(center, 2))
            .spectatorPosition(calculateSpectatorPosition(center))
            .ffa(templateName.contains("ffa"))
            .breaking(true)
            .schematic(templateName + ".schem")
            .radiusDeleteBlocks(calculateRadius(templateName))
            .build();
        
        // Настраиваем FFA позиции если нужно
        if (arena.isFfa()) {
            arena.setFfaPositions(generateFFAPositions(center));
        }
        
        // Устанавливаем позицию для схемы
        arena.setSchematicPosition(new EntityPosition(center));
        
        // Сохраняем схему
        saveSchematic(templateName, center);
        
        return arena;
    }
    
    private Map<Integer, EntityPosition> generateFFAPositions(Location center) {
        Map<Integer, EntityPosition> positions = new HashMap<>();
        
        // Генерируем позиции по кругу
        int playerCount = 20; // Максимум 20 игроков
        double radius = 10.0;
        
        for (int i = 1; i <= playerCount; i++) {
            double angle = 2 * Math.PI * i / playerCount;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            
            Location spawnLoc = new Location(center.getWorld(), x, center.getY(), z);
            spawnLoc.setYaw((float) Math.toDegrees(angle + Math.PI));
            
            positions.put(i, new EntityPosition(spawnLoc));
        }
        
        return positions;
    }
    
    private EntityPosition calculateSpawnPosition(Location center, int playerNum) {
        Location spawn = center.clone();
        
        if (playerNum == 1) {
            spawn.add(5, 0, 0); // Справа от центра
            spawn.setYaw(270); // Смотреть на запад
        } else {
            spawn.add(-5, 0, 0); // Слева от центра
            spawn.setYaw(90); // Смотреть на восток
        }
        
        return new EntityPosition(spawn);
    }
    
    private String generateArenaName(String baseName) {
        return baseName.toLowerCase() + "_" + 
               world.getName() + "_" + 
               System.currentTimeMillis();
    }
    
    private Material getMaterialForTemplate(String template) {
        switch (template.toLowerCase()) {
            case "forest": return Material.OAK_LOG;
            case "desert": return Material.SANDSTONE;
            case "nether": return Material.NETHERRACK;
            default: return Material.STONE;
        }
    }
}
```
