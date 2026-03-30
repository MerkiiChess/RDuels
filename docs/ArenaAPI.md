# ArenaAPI

Актуальная документация находится в [API.md](/home/fallen/IdeaProjects/RDuels/docs/API.md), раздел `ArenaAPI`.

Ключевое поведение в релизе `2.0.0`:

- все поисковые методы `ArenaAPI` возвращают `Optional`
- `getFreeArena()` и `getFreeArenaFFA()` больше безопасно возвращают `Optional.empty()`, если свободных арен нет
- восстановление арены не выбрасывает stacktrace игрокам при проблемах со schematic, а пишет понятное сообщение в консоль
