# ⚔️ RDuels - Продвинутая система дуэлей для Minecraft 1.21

![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green?style=for-the-badge&logo=minecraft)
![Java](https://img.shields.io/badge/Java-21%2B-orange?style=for-the-badge&logo=openjdk)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)
![Paper](https://img.shields.io/badge/Paper-Supported-red?style=for-the-badge)
[![Discord](https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/DmxBGHCagv)

[![Build](https://github.com/MerkiiChess/RDuels/actions/workflows/build.yml/badge.svg)](https://github.com/MerkiiChess/RDuels/actions/workflows/build.yml)


**Мощный, гибкий и полностью настраиваемый плагин для дуэлей на вашем сервере.**  
RDuels предоставляет полный набор инструментов для создания соревновательной PvP-среды с уникальными возможностями для игроков и администраторов.

---

## 📥 Установка

1. **Скачайте** последний релиз `RDuels.jar`
2. **Поместите** файл в папку `plugins` вашего сервера
3. **Перезапустите** сервер (`/reload` или полный рестарт)
4. **Настройте** плагин через команды и конфигурационные файлы

### Требования
- **Minecraft Server:** Paper или совместимые сборки 1.21
- **Java:** версия 21 или выше
- **Дополнительно (рекомендуется):** WorldEdit для работы со схемами арен
- **Опционально:** Vault + плагин экономики (для стоимости пати и наград); PlaceholderAPI

---

## 🎯 Возможности

### Для игроков
- ✅ **Гибкие форматы дуэлей:** 1v1, 2v2, Party vs Party
- ✅ **Рейтинг Elo и ранги:** Прогрессия с тирами и наградами за достижение ранга
- ✅ **Очереди по китам:** `/queue` — обычные и ранкед-очереди с подбором по рейтингу
- ✅ **Эффекты убийства:** `/killeffect` — 20+ визуальных эффектов на выбор
- ✅ **Серии убийств:** Анонсы и награды за киллстрики
- ✅ **Реванш:** `/rematch` — быстрый повторный вызов последнего соперника
- ✅ **Скорборд:** Настраиваемый сайдбар со статистикой в лобби, бою и режиме зрителя
- ✅ **Настройки игрока:** `/settings` — скорборд, приём вызовов, Auto-GG, Auto-Requeue
- ✅ **Кастомные наборы (Kits):** Создавайте и сохраняйте свои уникальные комбинации
- ✅ **Система отрядов (Party):** Приглашайте друзей и сражайтесь вместе
- ✅ **Дуэли через таблички:** Быстрый старт боёв через интерактивные таблички
- ✅ **Локальная смена времени:** `/day` и `/night` для комфортной подготовки
- ✅ **Режим наблюдателя:** Наблюдайте за боями других игроков

### Для администраторов
- ✅ **Полный контроль над аренами:** До 20 точек спавна, поддержка схем WorldEdit
- ✅ **Правила китов (Kit Rules):** Сердца, множитель урона, UHC-режим, кулдауны предметов, лимит высоты билда, исчезающие блоки
- ✅ **Kill-сообщения по причине:** Настраиваемые сообщения смерти (лук, пустота, падение и т.д.)
- ✅ **Golden Head:** Усиленное лечащее яблоко с настраиваемыми эффектами
- ✅ **Режим Sumo:** Победа при выталкивании соперника в воду/пустоту (флаг арены)
- ✅ **Режим Bedwars (полный):** Кровати, генераторы ресурсов, магазин (`/shop`), командные апгрейды (`/upgrades`), персистентная броня/инструменты, победа последней команды
- ✅ **Режим Skywars:** Тиры лута (центр/острова), старт-предметы, рефилл сундуков, одна жизнь
- ✅ **Режим TNT Tag:** Бомба-пятнашки с фитилём, передача по удару, последний живой
- ✅ **Режим Flower Crown:** Кража цветов у соперника, защита своих, гонка до цели
- ✅ **Границы арены и защита от пустоты:** Возврат/поражение при выходе за границы
- ✅ **Экономика (Vault):** Стоимость создания пати и награды за победу
- ✅ **Party-чат и broadcast:** `/pc` для чата отряда, периодические напоминания
- ✅ **Случайные киты:** Кнопка «Случайный кит» с настройкой include/exclude
- ✅ **Цветная броня по киту:** Окрас кожаной брони через правила кита
- ✅ **Ограничение по мирам:** Отключение дуэлей в заданных мирах
- ✅ **Гибкая настройка табличек:** Автоматические очереди, различные режимы
- ✅ **Управление наборами:** Серверные и пользовательские наборы
- ✅ **Расширенная статистика:** Убийства, смерти, раунды, победы, поражения, Elo
- ✅ **Тиры наград:** Команды-награды за достижение рейтинговых порогов (`elo.yml`)
- ✅ **Модульная архитектура:** Каждый компонент работает независимо
- ✅ **API для разработчиков:** Полная интеграция с другими плагинами

---

## 📚 Команды и разрешения

### Основные команды игроков
| Команда | Разрешение | Описание |
|---------|------------|----------|
| `/duel <игрок>` | `duel.duel` | Вызов на дуэль |
| `/queue` (`/q`) | — | Меню очередей: ЛКМ — обычная, ПКМ — ранкед |
| `/queue leave` | — | Выйти из очереди |
| `/killeffect` | — | Меню выбора эффекта убийства |
| `/rematch` (`/again`) | — | Реванш с последним соперником |
| `/pc` (`/partychat`) | — | Переключить чат отряда |
| `/shop`, `/upgrades` | — | Магазин и командные улучшения (в бою Bedwars) |
| `/settings` | — | Настройки игрока: скорборд, приём вызовов, Auto-GG, Auto-Requeue |
| `/custom-kit` | `duel.customkit` | Меню кастомных наборов |
| `/party create` | `duel.party` | Создать отряд |
| `/spectator <игрок>` | `duel.spectate` | Наблюдать за боем |
| `/day`, `/night` | — | Локальная смена времени суток |

### Команды администраторов
| Команда | Разрешение | Описание |
|---------|------------|----------|
| `/r-duel arena create` | `r.duel.arena.create` | Создать арену |
| `/r-duel sign create` | `r.duel.sign.create` | Создать дуэльную табличку |
| `/r-duel savekit <название>` | `r.duel.savekit` | Сохранить набор |
| `/r-duel category` | `r.duel.category` | Контроль над категориями каастом китов |
| `/r-duel setlobby` | `r.duel.setlobby` | Установка лобби |

---

## ⚙️ Конфигурация

| Файл | Назначение |
|------|-----------|
| `settings.yml` | Общие настройки: длительности, спавны, база данных (SQLite/MySQL) |
| `kits.yml` | Серверные киты + секция `rules` (сердца, урон, UHC, кулдауны, высота билда, распад блоков) |
| `elo.yml` | Рейтинговая система: K-фактор, режим only-ranked, тиры и награды |
| `queue.yml` | Очереди: раунды, интервал матчмейкера, окно подбора по Elo |
| `scoreboard.yml` | Сайдбар: строки для лобби, боя и зрителя |
| `kill-effects.yml` | Эффекты убийства: список, иконки и права |
| `kill-streaks.yml` | Серии убийств: пороги, анонсы и награды |
| `kill-messages.yml` | Сообщения смерти по причине (DamageCause) |
| `golden-head.yml` | Golden Head: предмет и эффекты при поедании |
| `sumo.yml` | Режим Sumo: детект воды и дистанция падения |
| `skywars-loot.yml` | Режим Skywars: таблица лута сундуков |
| `bedwars.yml`, `bedwars-shop.yml`, `bedwars-upgrades.yml` | Bedwars: генераторы, магазин, апгрейды |
| `tnt-tag.yml` | TNT Tag: фитиль и кулдаун передачи |
| `flower-crown.yml` | Flower Crown: цель и материалы цветов |
| `boundary.yml` | Границы арены и защита от пустоты |
| `economy.yml` | Экономика (Vault): стоимость пати, награда за победу |
| `random-kits.yml` | Случайные киты: include/exclude пула |
| `party.yml` | Отряды: размер, чат-формат, broadcast |

Отдельные настройки: `disabled-worlds` в `settings.yml` (миры, где плагин отключён);
флаг `sumo: true` у арены в `arenas.yml`; секция `rules` у кита в `kits.yml`
(включая `armor-color`).
| `arenas.yml`, `duel.yml`, `party.yml`, `custom-kits.yml`, `menu.yml`, `messages.yml` | Арены, дуэли, отряды, кастомные киты, GUI, сообщения |

### PlaceholderAPI
`%duel_elo%`, `%duel_tier%`, `%duel_match_wins%`, `%duel_match_losses%`, `%duel_kills%`, `%duel_death%`,
`%duel_wins%`, `%duel_all_rounds%`, `%duel_opponent%`, `%duel_time%`, `%duel_kit%`,
`%duel_count_rounds%`, `%duel_played_count_rounds%`

> ⚠️ **Обновление с версий до 2.0:** в таблице `rduels` появились новые колонки
> (`elo`, `wins`, `losses`, `tier`, `scoreboard_enabled`, `duel_requests_enabled`,
> `auto_gg`, `auto_requeue`, `kill_effect`).
> На существующей базе добавьте их вручную (`ALTER TABLE`) или удалите старый файл БД —
> на свежей установке таблица создаётся автоматически.

---

## 🔌 API для разработчиков

RDuels предоставляет мощное API для интеграции с другими плагинами.

### Быстрый старт

```java
import ru.merkii.rduels;
import ru.merkii.rduels.core.duel.api;
import ru.merkii.rduels.api.DuelPlayer;

// Получение API модулей
DuelAPI duelAPI = RDuels.beanScope().get(DuelAPI.class);
PartyAPI partyAPI = RDuels.beanScope().get(PartyAPI.class);
ArenaAPI arenaAPI = RDuels.beanScope().get(ArenaAPI.class);

// Работа с игроком
Player player = event.getPlayer();
DuelPlayer duelPlayer = BukkitAdapter.adapt(player);

if (duelPlayer != null) {
    // Проверка состояния
    boolean inFight = duelPlayer.isFight();
    boolean inParty = duelPlayer.isPartyExists();
    
    // Статистика
    int kills = duelPlayer.getKills();
    duelPlayer.addKill();
}

// Доступ ко всем модулям
DuelAPI duelAPI = RDuels.beanScope().get(DuelAPI.class);          // Система дуэлей
PartyAPI partyAPI = RDuels.beanScope().get(PartyAPI.class);       // Система отрядов
SignAPI signAPI = RDuels.beanScope().get(SignAPI.class);          // Система табличек
ArenaAPI arenaAPI = RDuels.beanScope().get(ArenaAPI.class);       // Система арен
CustomKitAPI kitAPI = RDuels.beanScope().get(CustomKitAPI.class); // Кастомные наборы
EloAPI eloAPI = RDuels.beanScope().get(EloAPI.class);             // Рейтинг и тиры
QueueAPI queueAPI = RDuels.beanScope().get(QueueAPI.class);       // Очереди по китам

// Пример статистики

public class StatsDisplay {
    public void showStats(Player player) {
        DuelPlayer duelPlayer = BukkitAdapter.adapt(player);
        if (duelPlayer == null) return;
        
        double kdr = duelPlayer.getDeath() > 0 
            ? (double) duelPlayer.getKills() / duelPlayer.getDeath() 
            : duelPlayer.getKills();
            
        player.sendMessage(String.format("§6Статистика дуэлей:\n§fK/D: §a%d§7/§c%d\n§fKDR: §e%.2f", 
            duelPlayer.getKills(), duelPlayer.getDeath(), kdr));
    }
}

```
API:
[WIKI - DuelAPI](https://github.com/MerkiiChess/RDuels/blob/master/docs/DuelCoreAPI.md).
[WIKI - CustomKit](https://github.com/MerkiiChess/RDuels/blob/master/docs/CustomKit.md).
[WIKI - ArenaAPI](https://github.com/MerkiiChess/RDuels/blob/master/docs/ArenaAPI.md).
[WIKI - EloAPI](https://github.com/MerkiiChess/RDuels/blob/master/docs/EloAPI.md).
[WIKI - QueueAPI](https://github.com/MerkiiChess/RDuels/blob/master/docs/QueueAPI.md).
[WIKI - Developer API](https://github.com/MerkiiChess/RDuels/blob/master/docs/DeveloperAPI.md).
[WIKI - Конфигурация](https://github.com/MerkiiChess/RDuels/blob/master/docs/Configuration.md).
