📚 EloAPI - Полное руководство

🔧 EloAPI - Интерфейс рейтинговой системы

📋 Общая информация

EloAPI — публичный интерфейс рейтинговой системы (Elo) плагина RDuels. Предоставляет доступ к рейтингу игроков, тирам (рангам) и применению результатов матчей. Реализация и внутренности модуля могут меняться; стабильным контрактом является только этот интерфейс.

Рейтинг хранится вместе с остальной статистикой игрока в кэше (чтения не блокируют основной поток) и периодически сохраняется в базу данных.

🔍 Получение экземпляра API

```java

import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.core.elo.api.EloAPI;

EloAPI eloAPI = RDuels.beanScope().get(EloAPI.class);
```

📊 Методы EloAPI

```java

/**
 * Текущий рейтинг Elo игрока
 * @param player - обёртка игрока (BukkitAdapter.adapt(player))
 * @return рейтинг (по умолчанию новый игрок начинает с 1000)
 */
int getElo(DuelPlayer player);

/**
 * Отображаемое имя тира (ранга), соответствующего текущему рейтингу игрока
 * @param player - обёртка игрока
 * @return имя тира в формате MiniMessage или пустая строка, если тиры не настроены
 */
String getTierName(DuelPlayer player);

/**
 * Количество очков, которое получит победитель (и потеряет проигравший
 * до применения нижнего порога) при данных рейтингах
 * @param winnerElo - рейтинг победителя
 * @param loserElo - рейтинг проигравшего
 * @return изменение рейтинга (минимум 1)
 */
int calculateDelta(int winnerElo, int loserElo);

/**
 * Применение результата матча: перенос Elo между игроками, отправка
 * сообщений о рейтинге и выдача наград за новые достигнутые тиры.
 * Вызывать на основном потоке.
 * @param winner - победитель
 * @param loser - проигравший
 */
void applyMatchResult(DuelPlayer winner, DuelPlayer loser);
```

⚙️ Когда рейтинг применяется автоматически

Модуль сам слушает событие DuelStopFightEvent и применяет рейтинг при завершении боя:

- бой формата 1 на 1 (без отрядов и без 2v2);
- у боя есть победитель и проигравший (не ничья/таймаут);
- в elo.yml включён `enabled: true`;
- если `only-ranked: true` — только бои, созданные через ранкед-очередь (`/queue`, ПКМ по киту).

Победы и поражения матчей (`match_wins` / `match_losses`) записываются для всех форматов боёв, включая 2v2 и Party vs Party.

🏆 Тиры и награды

Тиры настраиваются в elo.yml списком по возрастанию порога:

```yaml
tiers:
  - name: "<yellow>Золото"
    elo: 1300
    commands:
      - "[console] give %player_name% gold_ingot 8"
```

- Награда выдаётся один раз — при первом достижении тира (прогресс хранится в базе данных).
- Команды поддерживают префиксы `[console]` / `[player]` и плейсхолдеры PlaceholderAPI.
- При скачке через несколько тиров за один матч награды выдаются за каждый пропущенный тир.

📈 Плейсхолдеры PlaceholderAPI

| Плейсхолдер | Описание |
|-------------|----------|
| `%duel_elo%` | Текущий рейтинг игрока |
| `%duel_tier%` | Имя текущего тира (legacy-цвета §) |
| `%duel_match_wins%` | Победы в матчах |
| `%duel_match_losses%` | Поражения в матчах |

💡 Пример использования

```java

import ru.merkii.rduels.RDuels;
import ru.merkii.rduels.adapter.DuelPlayer;
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;
import ru.merkii.rduels.core.elo.api.EloAPI;

public class RatingDisplay {

    public void showRating(Player player) {
        EloAPI eloAPI = RDuels.beanScope().get(EloAPI.class);
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (duelPlayer == null) return;

        int elo = eloAPI.getElo(duelPlayer);
        String tier = eloAPI.getTierName(duelPlayer);
        player.sendMessage("Рейтинг: " + elo + " | Ранг: " + tier);
    }
}
```
