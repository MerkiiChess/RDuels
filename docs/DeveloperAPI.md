📚 RDuels — API для разработчиков (v2.0)

Документ описывает публичные сервисы и точки расширения, добавленные в версии 2.0.
Все бины достаются из DI-контейнера плагина:

```java
import ru.merkii.rduels.RDuels;

MyService service = RDuels.beanScope().get(MyService.class);
```

Обёртка игрока `DuelPlayer` создаётся из `Player`:
```java
import ru.merkii.rduels.adapter.bukkit.BukkitAdapter;

DuelPlayer duelPlayer = BukkitAdapter.adapt(player); // null, если игрок null/оффлайн
```

Отдельные руководства: [DuelAPI](DuelCoreAPI.md) · [ArenaAPI](ArenaAPI.md) ·
[CustomKit](CustomKit.md) · [EloAPI](EloAPI.md) · [QueueAPI](QueueAPI.md).

---

## 📊 StatisticService — статистика и настройки игрока

Кэш всей статистики и предпочтений игрока. **Чтения не блокируют основной поток**
(значения берутся из памяти), запись в БД идёт асинхронно. Это единый источник правды,
пока игрок онлайн.

```java
import ru.merkii.rduels.statistic.StatisticService;

StatisticService stats = RDuels.beanScope().get(StatisticService.class);
UUID uuid = player.getUniqueId();

// Счётчики
int kills   = stats.getKills(uuid);
int deaths  = stats.getDeaths(uuid);
int winR    = stats.getWinRounds(uuid);
int allR    = stats.getAllRounds(uuid);
stats.addKill(uuid);            // мгновенный инкремент в кэше + async-persist

// Рейтинг / матчи
int elo     = stats.getElo(uuid);
int wins    = stats.getWins(uuid);
int losses  = stats.getLosses(uuid);
int tier    = stats.getTier(uuid);
int newElo  = stats.addElo(uuid, +12);   // дельта может быть отрицательной

// Настройки-тумблеры (персистятся)
boolean sb  = stats.isScoreboardEnabled(uuid);
stats.setScoreboardEnabled(uuid, false);
stats.isDuelRequestsEnabled(uuid);
stats.isAutoGg(uuid);
stats.isAutoRequeue(uuid);

// Выбранный эффект убийства
String effectId = stats.getKillEffect(uuid);
stats.setKillEffect(uuid, "lightning");
```

> Прямых счётчиков рекомендуется касаться через игровые события; произвольная запись
> `addKill/addElo` предназначена для интеграций, а не для обхода игрового цикла.

---

## 🏆 EloAPI — рейтинг

См. [EloAPI.md](EloAPI.md). Кратко:
```java
import ru.merkii.rduels.core.elo.api.EloAPI;

EloAPI elo = RDuels.beanScope().get(EloAPI.class);
int rating   = elo.getElo(duelPlayer);
String tier  = elo.getTierName(duelPlayer);
int delta    = elo.calculateDelta(winnerElo, loserElo);
elo.applyMatchResult(winner, loser);   // перенос рейтинга + награды за тир
```

## 🎯 QueueAPI — очереди

См. [QueueAPI.md](QueueAPI.md). Кратко:
```java
import ru.merkii.rduels.core.queue.api.QueueAPI;

QueueAPI queue = RDuels.beanScope().get(QueueAPI.class);
queue.joinQueue(duelPlayer, kitModel, /*ranked*/ true);
queue.leaveQueue(duelPlayer);
boolean inQueue = queue.isInQueue(duelPlayer);
```

---

## 💥 Kill Effects — свои эффекты убийства

Эффекты хранятся в `KillEffectRegistry` по id. Можно зарегистрировать собственный.

```java
import ru.merkii.rduels.core.killeffect.KillEffectRegistry;
import ru.merkii.rduels.core.killeffect.effect.KillEffect;

KillEffectRegistry registry = RDuels.beanScope().get(KillEffectRegistry.class);

registry.register(new KillEffect() {
    @Override public String id() { return "my_effect"; }
    @Override public void play(Player killer, Player victim, Location location) {
        location.getWorld().strikeLightningEffect(location);
    }
});
```
Чтобы эффект появился в меню `/killeffect`, добавьте запись с этим `id` в `kill-effects.yml`.
Проигрывается автоматически при убийстве, если игрок выбрал этот эффект.

---

## 💰 EconomyService — Vault

Тонкая обёртка над Vault. Полностью безопасна без Vault: `isEnabled()` вернёт `false`,
а операции станут no-op.

```java
import ru.merkii.rduels.core.economy.EconomyService;

EconomyService economy = RDuels.beanScope().get(EconomyService.class);
if (economy.isEnabled()) {
    double balance = economy.getBalance(player);
    if (economy.has(player, 100)) {
        economy.withdraw(player, 100);   // true при успехе
        economy.deposit(otherPlayer, 100);
    }
    String pretty = economy.format(100); // "100$" (формат провайдера)
}
```

---

## 🧱 RoundOutcomeService — программный проигрыш раунда

Позволяет засчитать игроку поражение в раунде без реальной смерти (пустота, выталкивание).
Проходит через штатное событие `DuelKillPlayerEvent`, поэтому раунды, статистика, Elo,
эффекты и стрики работают одинаково. Сейчас поддерживает **бои 1 на 1**.

```java
import ru.merkii.rduels.core.duel.RoundOutcomeService;

RoundOutcomeService outcome = RDuels.beanScope().get(RoundOutcomeService.class);
boolean applied = outcome.loseRoundOneVsOne(duelPlayer); // false, если не 1v1 / нет боя
```

---

## 🌍 WorldRestrictionService — миры

```java
import ru.merkii.rduels.core.world.WorldRestrictionService;

WorldRestrictionService worlds = RDuels.beanScope().get(WorldRestrictionService.class);
if (worlds.denyIfDisabled(player)) return;  // true + сообщение, если мир отключён
boolean disabled = worlds.isDisabled(player);
```

## 🎲 RandomKitService — случайный кит

```java
import ru.merkii.rduels.core.randomkit.RandomKitService;

RandomKitService random = RDuels.beanScope().get(RandomKitService.class);
KitModel kit = random.pickRandom();   // с учётом include/exclude; null если пул пуст
```

---

## 📡 События

Плагин вызывает стандартные Bukkit-события — на них можно подписаться:

| Событие | Когда |
|---------|-------|
| `DuelStartFightEvent` | Начало боя |
| `DuelStopFightEvent` | Завершение боя (есть `getWinner()`, `getLoser()`) |
| `DuelKillPlayerEvent` | Убийство/выбывание игрока в бою (Cancellable) |

```java
@EventHandler
public void onStop(DuelStopFightEvent event) {
    Player winner = event.getWinner();
    DuelFightModel fight = event.getDuelFightModel();
    boolean ranked = fight.isRanked();
    boolean fromQueue = fight.isFromQueue();
}
```

> Kill-эффекты, стрики, Elo, награды и авто-реквей внутри плагина реализованы именно
> как слушатели этих событий — тот же механизм доступен и сторонним плагинам.
