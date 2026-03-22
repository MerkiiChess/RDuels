# ⚔️ RDuels - Продвинутая система дуэлей для Minecraft 1.21

![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green?style=for-the-badge&logo=minecraft)
![Java](https://img.shields.io/badge/Java-8%2B-orange?style=for-the-badge&logo=openjdk)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)
![Paper](https://img.shields.io/badge/Paper-Supported-red?style=for-the-badge)
[![Discord](https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/DmxBGHCagv)


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

---

## 🎯 Возможности

### Для игроков
- ✅ **Гибкие форматы дуэлей:** 1v1, 2v2, Party vs Party
- ✅ **Кастомные наборы (Kits):** Создавайте и сохраняйте свои уникальные комбинации
- ✅ **Система отрядов (Party):** Приглашайте друзей и сражайтесь вместе
- ✅ **Дуэли через таблички:** Быстрый старт боёв через интерактивные таблички
- ✅ **Локальная смена времени:** `/day` и `/night` для комфортной подготовки
- ✅ **Режим наблюдателя:** Наблюдайте за боями других игроков

### Для администраторов
- ✅ **Полный контроль над аренами:** До 20 точек спавна, поддержка схем WorldEdit
- ✅ **Гибкая настройка табличек:** Автоматические очереди, различные режимы
- ✅ **Управление наборами:** Серверные и пользовательские наборы
- ✅ **Расширенная статистика:** Убийства, смерти, раунды, победы
- ✅ **Модульная архитектура:** Каждый компонент работает независимо
- ✅ **API для разработчиков:** Полная интеграция с другими плагинами

---

## 📚 Команды и разрешения

### Основные команды игроков
| Команда | Разрешение | Описание |
|---------|------------|----------|
| `/duel <игрок>` | `duel.duel` | Вызов на дуэль |
| `/custom-kit` | `duel.customkit` | Меню кастомных наборов |
| `/party create` | `duel.party` | Создать отряд |
| `/spectator <игрок>` | `duel.spectate` | Наблюдать за боем |

### Команды администраторов
| Команда | Разрешение | Описание |
|---------|------------|----------|
| `/r-duel arena create` | `r.duel.arena.create` | Создать арену |
| `/r-duel sign create` | `r.duel.sign.create` | Создать дуэльную табличку |
| `/r-duel savekit <название>` | `r.duel.savekit` | Сохранить набор |
| `/r-duel category` | `r.duel.category` | Контроль над категориями каастом китов |
| `/r-duel setlobby` | `r.duel.setlobby` | Установка лобби |

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
[WIKI - DuelAPI](https://github.com/MerkiiChess/RDuels/docs/DuelCoreAPI.md).
[WIKI - CustomKit](https://github.com/MerkiiChess/RDuels/docs/CustomKit.md).
[WIKI - ArenaAPI](https://github.com/MerkiiChess/RDuels/docs/ArenaAPI.md).
