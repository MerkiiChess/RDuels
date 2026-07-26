📚 Конфигурация RDuels — справочник

Документ описывает конфигурационные файлы, добавленные в версии 2.0 (Elo, очереди, скорборд,
эффекты, режимы, экономика и т.д.). Все файлы лежат в папке плагина и создаются автоматически
при первом запуске. Тексты поддерживают формат **MiniMessage** (`<red>`, `<gradient>` и т.п.),
если не указано иное.

> ⚠️ **Миграция базы данных.** В версии 2.0 в таблице `rduels` появились новые колонки:
> `elo`, `wins`, `losses`, `tier`, `scoreboard_enabled`, `duel_requests_enabled`, `auto_gg`,
> `auto_requeue`, `kill_effect`. На новой установке таблица создаётся сама; на существующей
> добавьте колонки через `ALTER TABLE` или удалите старый файл БД.

---

## ⚔️ Основные (существующие файлы, новые ключи)

### settings.yml
```yaml
# Миры, где команды плагина (/duel, /queue, /rematch) отключены
disabled-worlds: []
```

### duel.yml
```yaml
# Эффект слепоты во время отсчёта перед боем
match-found-blindness: true
```

### party.yml
```yaml
max-party-size: 10
# Формат строки чата отряда; (player) и (message)
chat-format: "<dark_aqua>[Пати] <gray>(player)<white>: (message)"
# Периодическое напоминание участникам об отряде
broadcast-enabled: false
broadcast-interval-seconds: 300
broadcast-message: "<gray>Вы состоите в пати. Напишите <white>/pc <gray>для чата пати."
```

### kits.yml — секция `rules`
Каждый серверный кит может иметь блок `rules` с правилами боя:
```yaml
kits:
  uhc:
    display-name: "<gold>UHC"
    slot: 0
    display-material: GOLDEN_APPLE
    items:
      0: { material: DIAMOND_SWORD, amount: 1 }
    rules:
      max-hearts: 10             # сердец (по умолчанию 10)
      damage-multiplier: 1.0     # множитель исходящего урона
      no-hunger: true            # UHC: голод не расходуется
      disable-crafting: false    # зарезервировано (крафт в бою запрещён глобально)
      build-height: -1           # лимит высоты билда над ареной (-1 = без лимита)
      block-decay-seconds: 0     # поставленные блоки исчезают через N сек (0 = выкл)
      item-cooldowns:            # кулдауны предметов в секундах
        GOLDEN_APPLE: 30
        ENDER_PEARL: 15
      armor-color: "255,0,0"     # окрас кожаной брони кита (R,G,B); пусто = без окраса
```

### arenas.yml — флаги режимов
```yaml
sumo: true          # режим сумо (падение в воду/пустоту = поражение)
bedwars: true       # режим bedwars (кровать = жетон возрождения)
skywars: true       # режим skywars (лут-сундуки, одна жизнь)
tnt-tag: true       # режим TNT Tag (бомба-пятнашки)
flower-crown: true  # режим Flower Crown (кража цветов)
```
Режимы 1-на-1. Для bedwars кровати ставятся у спавнов сторон (`one-position`/`two-position`);
владелец кровати определяется по близости к спавну. Для skywars сундуки заполняются
из `skywars-loot.yml`. В обоих режимах число игр принудительно = 1.

---

## 🏆 elo.yml — рейтинг и тиры
```yaml
enabled: true
# true — рейтинг меняют только ранкед-бои (из очереди)
only-ranked: false
# K-фактор формулы Elo (макс. изменение за бой)
k-factor: 32
# Ниже этого рейтинг не опускается
min-elo: 0
# Тиры наград (по возрастанию elo). Награда выдаётся 1 раз при первом достижении.
tiers:
  - name: "<yellow>Золото"
    elo: 1300
    commands:
      - "[console] give %player_name% gold_ingot 8"
```
`commands`: префикс `[console]` или `[player]`, поддержка PlaceholderAPI.

---

## 🎯 queue.yml — очереди по китам
```yaml
enabled: true
match-interval-ticks: 20        # период матчмейкера (20 = раз в сек)
num-games: 1                    # раундов в матче из очереди
ranked-elo-range: 100           # стартовое окно подбора по Elo
ranked-elo-range-per-second: 10 # расширение окна за секунду ожидания
```

---

## 📊 scoreboard.yml — сайдбар
```yaml
enabled: true
update-interval-ticks: 20
title: "<gold><bold>RDuels"
lobby-lines: [ ... ]      # строки в лобби
fight-lines: [ ... ]      # строки в бою
spectator-lines: [ ... ]  # строки для зрителя
```
Плейсхолдеры (везде): `(player) (elo) (tier) (kills) (deaths) (wins) (losses)
(win_rounds) (all_rounds) (online)`. Только в бою: `(opponent) (kit) (time) (round) (rounds)`.
Также работают плейсхолдеры PlaceholderAPI (`%...%`).

---

## 💥 kill-effects.yml — эффекты убийства
```yaml
enabled: true
effects:
  - id: lightning        # id одного из встроенных эффектов
    material: LIGHTNING_ROD
    name: "<yellow>Молния"
    permission: ""       # право для выбора (пусто = всем)
```
**Встроенные id:** `lightning, explosion, firework, flame, hearts, blood, soul, tornado,
snowstorm, rainbow, enchant, wither, dragon_breath, portal, smoke, totem, sonic_boom, notes,
angry_villager, happy_villager, critical, witch, water_splash, bubble, cloud, slime, cherry,
electric_spark, inferno`. Выбор через `/killeffect`.

---

## 🔥 kill-streaks.yml — серии убийств
```yaml
enabled: true
broadcast-global: false   # true — всему серверу, false — участникам боя
tiers:
  - count: 5
    message: "<gold>(player) <gray>на серии из <white>(streak)"
    commands: []
```
Плейсхолдеры: `(player) (streak)`.

---

## ⚰️ kill-messages.yml — сообщения смерти по причине
```yaml
enabled: true
broadcast-global: false
default-message: "<gray>(victim) <white>погибает от рук <gray>(killer)"
environment-name: "<dark_gray>окружения"   # подставляется в (killer) при смерти от окружения
causes:
  projectile: "<gray>(killer) <white>прострелил <gray>(victim)"
  void: "<gray>(victim) <white>упал в пустоту"
```
Ключи `causes` — имена `DamageCause` в нижнем регистре (`entity_attack, projectile, void,
fall, fire_tick, lava, magic, block_explosion, entity_explosion, drowning`, …).
Плейсхолдеры: `(victim) (killer)`.

---

## 🍎 golden-head.yml — усиленное яблоко
```yaml
enabled: true
material: GOLDEN_APPLE
display-name: "<gold>Golden Head"   # пусто = матч только по материалу
effects:
  - "ABSORPTION,120,0"              # ТИП,секунды,усиление(с 0)
  - "REGENERATION,8,1"
```
Предмет кладётся в кит обычным способом; эффекты добавляются поверх ванильных при поедании.

---

## 🧱 boundary.yml — границы и защита от пустоты
```yaml
enabled: true
void-min-y: 0                # ниже — «пустота»
void-action: KILL            # KILL (проигрыш раунда) или TELEPORT_BACK
max-radius: 0                # горизонтальный радиус от центра арены (0 = выкл)
warn-distance: 5             # за сколько блоков предупреждать (0 = выкл)
warn-message: "<red>Граница арены! Осталось <white>(blocks) <red>блоков"
```

---

## 🤼 sumo.yml — режим сумо
```yaml
enabled: true
detect-water: true    # касание воды = поражение
fall-distance: 3      # падение на N блоков ниже спавна = поражение
```
Активируется флагом `sumo: true` у арены. Только 1 на 1.

---

## 💰 economy.yml — Vault
```yaml
enabled: true
party-cost: 0.0             # стоимость /party create (0 = бесплатно)
win-reward: 0.0            # награда победителю (0 = выкл)
win-reward-ranked-only: true
```
Требуется Vault + плагин экономики; без них модуль неактивен.

---

## 🛏️ Bedwars — полный режим

Три файла: `bedwars.yml` (генераторы/респавн), `bedwars-shop.yml` (магазин), `bedwars-upgrades.yml`
(командные улучшения). Активируется флагом `bedwars: true` у арены.

**Настройка арены:** поставьте `breaking: true` (нужны блоки для мостов/подкопа); кровати — у
спавнов сторон (`one-position` / `two-position`), владелец определяется по близости; блоки-маркеры
генераторов (по умолчанию `IRON_BLOCK`/`GOLD_BLOCK`/`DIAMOND_BLOCK`/`EMERALD_BLOCK`) — ресурс спавнится
на блок выше маркера. Команды в игре: `/shop`, `/upgrades`.

### bedwars.yml
```yaml
enabled: true
respawn-seconds: 4         # задержка возрождения (пока кровать цела)
scan-radius: 40            # поиск маркеров генераторов вокруг центра арены
generators:
  IRON_BLOCK:              # ключ = материал блока-маркера
    resource: IRON         # IRON | GOLD | DIAMOND | EMERALD
    interval: 24           # базовый интервал спавна (тики)
    team: true             # базовый генератор команды (ускоряется апгрейдом Forge)
    max-nearby: 48         # кап предметов рядом
```

### bedwars-shop.yml
```yaml
items:
  - slot: 10
    material: IRON_SWORD
    name: "<white>Железный меч"
    cost-resource: GOLD    # IRON | GOLD | DIAMOND | EMERALD
    cost-amount: 7
    enchants: { SHARPNESS: 1 }
    # category: armor|pickaxe|axe|shears — сохраняется между смертями; tier — уровень
```
Броня (кроме шлема/нагрудника — они всегда цветная кожа команды), инструменты и ножницы
сохраняются при возрождении; меч/блоки теряются.

### bedwars-upgrades.yml
```yaml
upgrades:
  - type: PROTECTION       # SHARPNESS | PROTECTION | HASTE | FORGE | HEAL_POOL
    slot: 1
    material: DIAMOND_CHESTPLATE
    name: "<blue>Защита"
    levels:
      - { cost-resource: DIAMOND, cost-amount: 2, value: 1 }
      - { cost-resource: DIAMOND, cost-amount: 4, value: 2 }
```
Апгрейды командные: Sharpness (меч), Protection (броня), Haste (эффект), Forge (скорость
генераторов), Heal Pool. `value` — сила уровня (уровень зачара / амплификатор / шаг forge).

---

## 💣 tnt-tag.yml — TNT Tag
```yaml
enabled: true
fuse-seconds: 15         # длина фитиля бомбы
pass-cooldown-ticks: 20  # нельзя сразу передать бомбу обратно
```
Флаг `tnt-tag: true`. Держатель бомбы светится; передача — по удару. Когда фитиль истёк,
держатель взрывается и выбывает, бомба переходит случайному живому. PvP-урон отключён,
выбывает только держатель. Последний живой — победитель. Нужно 2+ игроков.

## 🌸 flower-crown.yml — Flower Crown
```yaml
enabled: true
flowers-to-win: 5        # сколько цветов украсть для победы
flower-materials: []     # пусто = все ванильные мелкие/высокие цветы
```
Флаг `flower-crown: true`. Цветы принадлежат ближайшей к ним стороне; сломать чужой цветок =
украсть (очко), свои ломать нельзя. Первая сторона до `flowers-to-win` — победитель.

## 🏝️ skywars-loot.yml — лут Skywars
```yaml
enabled: true
search-radius: 20          # полурадиус куба вокруг центра для поиска сундуков (скан блоков!)
mid-radius: 8              # сундуки ближе к центру = mid-лут (лучше); 0 = без тиров
refill-seconds: 0          # рефилл каждые N сек (0 = только на старте)
refill-announce: true
refill-message: "<yellow>Сундуки пополнены!"
min-items-per-chest: 3
max-items-per-chest: 6
starter-items:             # если заданы — заменяют кит; броня авто-экипируется
  - { material: WOODEN_SWORD, amount: 1 }
  - { material: OAK_PLANKS, amount: 16 }
loot:                      # островные (внешние) сундуки
  - material: IRON_SWORD
    weight: 3
  - material: ARROW
    min-amount: 4
    max-amount: 16
    weight: 4
mid-loot:                  # центральные сундуки (лучше); пусто = как loot
  - material: DIAMOND_SWORD
    weight: 2
```
Активируется флагом `skywars: true` у арены. Одна жизнь; пустота = смерть (Boundary в
skywars/bedwars не вмешивается). Держите `search-radius` небольшим — идёт скан блоков.

---

## 🎲 random-kits.yml — случайные киты
```yaml
enabled: true
include: []   # выбирать только из этих китов (display-name); пусто = все
exclude: []   # никогда не выбирать эти киты
```
Используется кнопкой «Случайный кит» в меню дуэли и как fallback при невыбранном ките.
