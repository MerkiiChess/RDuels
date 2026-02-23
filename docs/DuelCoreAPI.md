📚 DuelCore API - Полное руководство
🔧 DuelAPI - Основной интерфейс управления дуэлями
📋 Общая информация

DuelAPI является центральным интерфейсом для управления всеми аспектами системы дуэлей. Предоставляет методы для работы с боями, запросами, аренами, наборами и зрителями.
🔍 Получение экземпляра API
```java

import ru.merkii.rduels.api.Duel;
import ru.merkii.rduels.core.duel.api.DuelAPI;

DuelAPI duelAPI = Duel.getDuelAPI();
```
📊 Методы DuelAPI
Работа с наборами (Kits)
```java

/**
 * Получение модели набора по имени
 * @param kitName - название набора
 * @return KitModel или null если не найден
 */
@Nullable
KitModel getKitFromName(String kitName);

/**
 * Сохранение серверного набора
 * @param player - игрок, чей инвентарь сохраняется
 * @param kitName - название набора
 */
void saveKitServer(Player player, String kitName);

/**
 * Проверка существования набора по имени
 * @param kitName - название набора
 * @return true если набор существует
 */
boolean isKitNameContains(String kitName);

/**
 * Получение свободного слота для набора в меню
 * @return индекс слота или -1 если нет свободных
 */
int getFreeSlotKit();

/**
 * Получение случайного набора
 * @return случайный KitModel
 */
KitModel getRandomKit();
```
Управление боями
```java

/**
 * Начало дуэли 1 на 1 или Party vs Party
 * @param duelRequest - запрос на дуэль
 */
void startFight(DuelRequest duelRequest);

/**
 * Начало боя 2 на 2 (четыре игрока)
 * @param player, player2, player3, player4 - участники
 * @param duelRequest - запрос на дуэль
 */
void startFightFour(Player player, Player player2, Player player3, Player player4, DuelRequest duelRequest);

/**
 * Переход к следующему раунду
 * @param duelFight - модель текущего боя
 */
void nextRound(DuelFightModel duelFight);

/**
 * Остановка боя с определением победителя
 * @param duelFightModel - модель боя
 * @param winner - победитель
 * @param loser - проигравший
 */
void stopFight(DuelFightModel duelFightModel, Player winner, Player loser);
```
Работа с запросами на дуэль
```java

/**
 * Добавление запроса на дуэль
 * @param duelRequest - запрос на дуэль
 */
void addRequest(DuelRequest duelRequest);

/**
 * Удаление запроса на дуэль
 * @param duelRequest - запрос на удаление
 */
void removeRequest(DuelRequest duelRequest);

/**
 * Получение всех запросов для игрока
 * @param receiver - игрок-получатель
 * @return список запросов или null
 */
@Nullable
List<DuelRequest> getRequestsFromReceiver(Player receiver);

/**
 * Получение конкретного запроса
 * @param sender - отправитель
 * @param receiver - получатель
 * @return DuelRequest или null
 */
@Nullable
DuelRequest getRequestFromSender(Player sender, Player receiver);
````
Управление аренами
```java

/**
 * Получение свободной арены
 * @return свободная ArenaModel или null
 */
@Nullable
ArenaModel getFreeArena();

/**
 * Получение свободной арены по имени
 * @param name - название арены
 * @return ArenaModel или null
 */
@Nullable
ArenaModel getFreeArenaName(String name);

/**
 * Получение свободной FFA арены
 * @return FFA ArenaModel или null
 */
@Nullable
ArenaModel getFreeArenaFFA();
```
Проверка состояния игроков
```java

/**
 * Проверка участия игрока в бою
 * @param player - проверяемый игрок
 * @return true если игрок в бою
 */
boolean isFightPlayer(Player player);

/**
 * Получение модели боя по игроку
 * @param player - участник боя
 * @return DuelFightModel или null
 */
@Nullable
DuelFightModel getFightModelFromPlayer(Player player);

/**
 * Получение противника в бою
 * @param duelFightModel - модель боя
 * @param player - текущий игрок
 * @return противник
 */
Player getOpponentFromFight(DuelFightModel duelFightModel, Player player);

/**
 * Получение противника (упрощенный метод)
 * @param player - текущий игрок
 * @return противник или null
 */
@Nullable
Player getOpponentFromFight(Player player);
```
Управление зрителями (спектаторами)
```java

/**
 * Добавление зрителя к бою
 * @param player - зритель
 * @param duelFightModel - модель боя
 */
void addSpectate(Player player, DuelFightModel duelFightModel);

/**
 * Удаление зрителя из боя
 * @param player - зритель
 * @param duelFightModel - модель боя
 * @param fighting - true если бой продолжается
 */
void removeSpectate(Player player, DuelFightModel duelFightModel, boolean fighting);

/**
 * Проверка является ли игрок зрителем
 * @param player - проверяемый игрок
 * @return true если является зрителем
 */
boolean isSpectate(Player player);

/**
 * Получение модели боя по зрителю
 * @param player - зритель
 * @return DuelFightModel или null
 */
@Nullable
DuelFightModel getDuelFightModelFromSpectator(Player player);
```
Подготовка игроков к бою
```java

/**
 * Подготовка списка игроков к бою
 * @param players - список игроков
 */
void preparationToFight(List<Player> players);

/**
 * Подготовка нескольких игроков к бою
 * @param players - игроки для подготовки
 */
void preparationToFight(Player... players);

/**
 * Подготовка двух отрядов к бою
 * @param senderParty - отряд отправителя
 * @param receiverParty - отряд получателя
 */
void preparationToFight(PartyModel senderParty, PartyModel receiverParty);
```
Утилитные методы
```java

/**
 * Получение победителя в бою
 * @param duelFightModel - модель боя
 * @param loser - проигравший
 * @return победитель
 */
Player getWinnerFromFight(DuelFightModel duelFightModel, Player loser);

/**
 * Получение проигравшего в бою
 * @param duelFightModel - модель боя
 * @param winner - победитель
 * @return проигравший
 */
Player getLoserFromFight(DuelFightModel duelFightModel, Player winner);

/**
 * Получение случайной точки спавна
 * @return Location для телепортации
 */
Location getRandomSpawn();

/**
 * Выдача стартовых предметов игроку
 * @param player - целевой игрок
 */
void giveStartItems(Player player);

/**
 * Получение телепорт-планировщика для боя
 * @param duelFightModel - модель боя
 * @return Optional с DuelTeleportScheduler
 */
Optional<DuelTeleportScheduler> getTeleportSchedulerFromFight(DuelFightModel duelFightModel);
```
🔒 Управление движением игроков
```java

/**
 * Добавление игрока в список немогущих двигаться
 * @param player - игрок для блокировки
 */
void addNoMove(Player player);

/**
 * Удаление игрока из списка немогущих двигаться
 * @param player - игрок для разблокировки
 */
void removeNoMove(Player player);

/**
 * Проверка блокировки движения игрока
 * @param player - проверяемый игрок
 * @return true если движение заблокировано
 */
boolean isNoMovePlayer(Player player);
```

🏗️ Модели данных
DuelFightModel

Модель текущего боя:

    Участники (игроки или отряды)

    Текущий раунд и общее количество раундов

    Используемый набор

    Арена

    Зрители

    Таймеры

DuelRequest

Модель запроса на дуэль:

    Отправитель и получатель

    Тип дуэли (1v1, 2v2, Party vs Party)

    Набор для боя

    Арена

    Количество раундов

KitModel

Модель набора:

    Название и отображаемое имя

    Слот в меню

    Иконка (Material)

    Предметы инвентаря

ArenaModel

Модель арены:

    Название и отображаемое имя

    Позиции для игроков (до 20 позиций)

    Позиция для зрителей

    Настройки FFA (Free For All)

    Настройки восстановления (breaking)

💡 Примеры использования
Пример 1: Создание дуэли
```java

public class DuelManager {
    private DuelAPI duelAPI;
    
    public void createDuel(Player sender, Player receiver, String arenaName) {
        // Получаем набор
        KitModel kit = duelAPI.getKitFromName("Default");
        
        // Получаем арену
        ArenaModel arena = duelAPI.getFreeArenaName(arenaName);
        
        if (kit != null && arena != null) {
            // Создаем запрос на дуэль
            DuelRequest request = new DuelRequest(sender, receiver, 3, kit, arena);
            
            // Добавляем запрос
            duelAPI.addRequest(request);
        }
    }
}
```

Пример 2: Обработка завершения боя
```java

public class FightListener implements Listener {
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DuelAPI duelAPI = Duel.getDuelAPI();
        
        // Проверяем, был ли игрок в дуэли
        if (duelAPI.isFightPlayer(player)) {
            DuelFightModel fight = duelAPI.getFightModelFromPlayer(player);
            Player killer = player.getKiller();
            
            if (fight != null && killer != null) {
                // Определяем победителя и проигравшего
                Player winner = duelAPI.getWinnerFromFight(fight, player);
                Player loser = duelAPI.getLoserFromFight(fight, killer);
                
                // Останавливаем бой
                duelAPI.stopFight(fight, winner, loser);
                
                // Обновляем статистику
                DuelPlayer duelPlayer = Duel.getDuelPlayer(killer);
                if (duelPlayer != null) {
                    duelPlayer.addKill();
                }
            }
        }
    }
}
```
Пример 3: Работа со зрителями
```java

public class SpectatorManager {
    private DuelAPI duelAPI;
    
    public void toggleSpectator(Player spectator, Player target) {
        // Проверяем, находится ли цель в бою
        if (duelAPI.isFightPlayer(target)) {
            DuelFightModel fight = duelAPI.getFightModelFromPlayer(target);
            
            if (fight != null) {
                if (duelAPI.isSpectate(spectator)) {
                    // Удаляем из зрителей
                    duelAPI.removeSpectate(spectator, fight, true);
                } else {
                    // Добавляем в зрители
                    duelAPI.addSpectate(spectator, fight);
                }
            }
        }
    }
}
```

Пример 4: Создание кастомного набора
```java

public class KitCreator {
    public void createCustomKit(Player player, String kitName) {
        DuelAPI duelAPI = Duel.getDuelAPI();
        
        // Проверяем, существует ли уже набор
        if (!duelAPI.isKitNameContains(kitName)) {
            // Сохраняем текущий инвентарь как набор
            duelAPI.saveKitServer(player, kitName);
            player.sendMessage("Набор '" + kitName + "' успешно создан!");
        } else {
            player.sendMessage("Набор с таким именем уже существует!");
        }
    }
}
```

🚀 Расширенные сценарии
Автоматический подбор противников
```java

public class Matchmaking {
    public void findOpponent(Player player) {
        DuelAPI duelAPI = Duel.getDuelAPI();
        
        // Ищем случайного игрока в лобби
        List<Player> availablePlayers = Bukkit.getOnlinePlayers().stream()
            .filter(p -> !duelAPI.isFightPlayer(p))
            .filter(p -> !p.equals(player))
            .collect(Collectors.toList());
        
        if (!availablePlayers.isEmpty()) {
            Player opponent = availablePlayers.get(
                ThreadLocalRandom.current().nextInt(availablePlayers.size())
            );
            
            // Создаем дуэль со случайным набором и ареной
            KitModel randomKit = duelAPI.getRandomKit();
            ArenaModel randomArena = duelAPI.getFreeArena();
            
            if (randomKit != null && randomArena != null) {
                DuelRequest request = new DuelRequest(
                    player, opponent, 3, randomKit, randomArena
                );
                duelAPI.addRequest(request);
            }
        }
    }
}
```

Система турниров
```java

public class Tournament {
    private Map<UUID, Integer> scores = new HashMap<>();
    private DuelAPI duelAPI;
    
    public void startTournament(List<Player> participants) {
        // Создаем пары для первого раунда
        for (int i = 0; i < participants.size(); i += 2) {
            if (i + 1 < participants.size()) {
                Player player1 = participants.get(i);
                Player player2 = participants.get(i + 1);
                
                // Создаем дуэль для турнира
                DuelRequest request = new DuelRequest(
                    player1, player2, 1, // Один раунд для турнира
                    duelAPI.getKitFromName("Tournament"),
                    duelAPI.getFreeArena()
                );
                duelAPI.addRequest(request);
            }
        }
    }
}
```
