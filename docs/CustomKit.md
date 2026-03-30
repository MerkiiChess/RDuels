# CustomKit API

Актуальная документация находится в [API.md](/home/fallen/IdeaProjects/RDuels/docs/API.md), раздел `CustomKitAPI`.

Что изменилось к релизу `2.0.0`:

- выбранный кастомный кит больше не хранится как строка `"NULL"` внутри runtime-логики
- добавлен безопасный Optional-метод `getSelectedKit(DuelPlayer)`
- `getKitModel(DuelPlayer)` теперь корректно возвращает `null`, если у игрока не выбран или пустой кит
