📚 QueueAPI - Полное руководство

🔧 QueueAPI - Интерфейс очередей по китам

📋 Общая информация

QueueAPI — публичный интерфейс системы очередей RDuels. Игроки встают в очередь на конкретный кит (обычную или ранкед), а матчмейкер автоматически подбирает соперника и запускает бой. Стабильным контрактом является только этот интерфейс; внутренности модуля могут меняться.

🔍 Получение экземпляра API

```java

import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.queue.api.QueueAPI;

QueueAPI queueAPI = RDuels.beanScope().get(QueueAPI.class);
```

📊 Методы QueueAPI

```java

/**
 * Постановка игрока в очередь на кит.
 * Игрок в бою или уже стоящий в очереди получит сообщение об ошибке.
 * @param player - обёртка игрока (BukkitAdapter.adapt(player))
 * @param kitModel - серверный кит (DuelAPI#getKitFromName)
 * @param ranked - true для ранкед-очереди (подбор по Elo, бой влияет на рейтинг)
 */
void joinQueue(DuelPlayer player, KitModel kitModel, boolean ranked);

/**
 * Выход игрока из очереди (любой)
 * @param player - обёртка игрока
 * @return true, если игрок действительно стоял в очереди
 */
boolean leaveQueue(DuelPlayer player);

/**
 * Проверка, стоит ли игрок в очереди
 * @param player - обёртка игрока
 * @return true, если игрок в очереди
 */
boolean isInQueue(DuelPlayer player);
```

⚙️ Как работает матчмейкер

Матчмейкер запускается по таймеру (`match-interval-ticks` в queue.yml) и на каждом тике:

1. Удаляет из очередей вышедших с сервера игроков.
2. Обычная очередь: соединяет двух первых игроков с одинаковым китом (FIFO).
3. Ранкед-очередь: соединяет игроков с одинаковым китом, если разница их Elo
   не превышает окно `ranked-elo-range + ranked-elo-range-per-second × секунды_ожидания` —
   чем дольше игрок ждёт, тем шире допустимая разница.
4. Найденной паре отправляется сообщение, оба игрока убираются из очередей,
   и бой запускается на любой свободной арене с флагом ranked (для ранкед-очереди).

Количество раундов матча из очереди задаётся параметром `num-games` в queue.yml.

🎮 Команды

| Команда | Описание |
|---------|----------|
| `/queue` (`/q`) | Открыть меню очередей: ЛКМ по киту — обычная очередь, ПКМ — ранкед |
| `/queue leave` (`/q leave`) | Выйти из очереди |

⚙️ Конфигурация (queue.yml)

```yaml
enabled: true
match-interval-ticks: 20        # период матчмейкера (20 = раз в секунду)
num-games: 1                    # раундов в матче из очереди
ranked-elo-range: 100           # стартовое окно подбора по Elo
ranked-elo-range-per-second: 10 # расширение окна за секунду ожидания
```

💡 Пример использования

```java

import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.queue.api.QueueAPI;
import ru.merkii.rduels.model.KitModel;

public class AutoQueue {

    public void queueForKit(Player player, String kitName) {
        QueueAPI queueAPI = RDuels.beanScope().get(QueueAPI.class);
        DuelAPI duelAPI = RDuels.beanScope().get(DuelAPI.class);

        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        KitModel kit = duelAPI.getKitFromName(kitName);
        if (duelPlayer == null || kit == null) return;

        // Поставить в ранкед-очередь
        queueAPI.joinQueue(duelPlayer, kit, true);
    }
}
```
