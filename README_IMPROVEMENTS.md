# Improvements Log

## SOLID fixes 
- LevelGraphics OCP/DIP: убраны instanceof-ветки; введены `RenderableFactory`/`GameObjectRenderableFactory`, создание view делегировано абстракции; `LevelGraphics` осталась только наблюдателем и хранителем Renderable-объектов.
- GameField DIP/OCP: загрузка карты вынесена в `MapProvider` (`TmxMapProvider` по умолчанию); `GameField` больше не создаёт `TmxMapLoader` и не знает путь к ресурсу.
- BulletModel DIP: пуля больше не зависит от `WorldModel`; добавлен `CollisionContext` (границы/коллизии/урон), реализован в `WorldModel`; пули обновляются через контекст.
- WorldModel SRP: стрельба/урон в `CombatSystem` (`DefaultCombatSystem`), шаг пуль в `BulletSimulator`; `WorldModel` хранит состояние и публикует события.
- Конфигурация: `RenderingConfiguration` создаёт `MapProvider`, `GameDesktopLauncher` инжектирует фабрику рендеров и провайдер карты; новые абстракции подключены через Spring.

## Architecture Overview ( актуально на Dec 2025)
- Слои и зависимости:
	- Core Model: `WorldModel` (состояние, наблюдатели), `CombatSystem` (стрельба/урон), `BulletSimulator` (обновление пуль), сущности (`TankModel`, `BulletModel`, `Obstacle` и т.п.). Модель не знает о рендере и вводе.
	- Input: `InputHandler` читает `InputSource`, раскладывает `InputCommand` в `CommandQueue`. Команды исполняются в начале тика.
	- AI: `AIHandler` использует `BotStrategy`/`AIShootingPolicy`, пишет команды в ту же очередь, обеспечивая порядок input → AI → мир.
	- World Updates: `GameSession` исполняет очередь команд, вызывает `WorldModel.tick`, затем уведомляет view. Боевая логика делегирована в `CombatSystem`/`BulletSimulator`.
	- View/Rendering: `LevelGraphics` реализует `WorldObserver`, хранит renderable-объекты, не знает модельные типы; `RenderableFactory`/`GameObjectRenderableFactory` создают renderables. `GameField` рендерит карту (`MapProvider` → `TmxMapProvider`) и задаёт правила движения (`TileMovement`).
	- Resources: `RenderingConfiguration` создаёт `SpriteBatch`, текстуры, pixel-texture; все ресурсы `@Bean(destroyMethod="dispose")` для корректного lifecycle.
	- Composition: Spring-конфиги (`CoreConfiguration`, `RenderingConfiguration`, `InputConfiguration`, `GameSessionConfiguration`, `AiConfiguration`) собирают зависимости; `GameDesktopLauncher` поднимает контекст и делегирует в `GameSession`.
- Поток данных за кадр: Input/AI пишут команды → `GameSession` исполняет очередь → `WorldModel` обновляется и через `WorldObserver` сообщает `LevelGraphics` → `LevelGraphics` обновляет renderables → `GameField` и renderables рисуются через общий `SpriteBatch`.
- Расширяемость:
	- Новые сущности рендерятся через добавление `RenderableCreation` в `GameObjectRenderableFactory` без правок `LevelGraphics`.
	- Альтернативные карты подключаются через реализацию `MapProvider` (например, другой формат или ресурс).
	- Поведение AI меняется заменой `BotStrategy`/`AIShootingPolicy` в конфиге.
	- Правила боя меняются заменой `CombatSystem`; физика пуль — заменой `BulletSimulator`.
	- Источники ввода подменяются реализациями `InputSource` (для тестов или другого UI).

## Spring IoC and Lifecycle (old)
- Было: один GameConfig с минимальной конфигурацией; GameDesktopLauncher помечен @Component, но создавался вручную через getBean; ресурсы batch/texture создавались и диспоузились вручную; рендеринговые зависимости создавались через new внутри лаунчера.
- Стало: модульные конфиги Core/Ai/Input/Rendering/GameSession; все зависимости (batch, field, level loader, стратегии, world factory, текстуры) инжектятся через Spring; ресурсы объявлены как @Bean с destroyMethod=dispose; main поднимает GameSessionConfiguration в try-with-resources для корректного закрытия контекста.
- Почему: корректное IoC-разделение, управление жизненным циклом ресурсов через Spring, отсутствие ручного new/dispose, соблюдение SRP и DI.

## Ресурсы рендеринга
- Было: Tank/TreeObstacle диспоузили текстуры, созданные внутри лаунчера; HealthBarTank и BulletRenderable генерировали собственные пиксели через статические Pixmap/Texture; общий batch создавался в лаунчере.
- Стало: текстуры, batch и pixel-текстура создаются один раз в RenderingConfiguration и инжектятся; Tank/TreeObstacle не диспоузят шареные текстуры; HealthBarTank и BulletRenderable используют общую pixel-текстуру из Spring.
- Почему: избежать двойного dispose и утечек, единообразное создание/уничтожение ресурсов, чистая инъекция вместо статического состояния.

## Запуск после IoC-рефакторинга
- Было: libGDX ресурсы создавались до инициализации GDX-контекста, падение UnsatisfiedLinkError.
- Стало: batch/field/textures/loader запрашиваются через `ObjectProvider` внутри `GameDesktopLauncher.create()`, все бины помечены @Lazy; добавлена явная инициализация player renderable до первого кадра.
- Почему: корректный порядок инициализации GDX и отсутствие NPE на первом кадре; `./gradlew.bat run` проходит.

## Архитектура: разделение логики и рендера
- Было: `GameDesktopLauncher` — God Object (логика, ввод, AI, рендер, observer), создание view внутри лаунчера.
- Стало: добавлен `GameSession` (tick ввода, AI, мира, вычисление occupied/reserved), `LevelGraphics` реализует `WorldObserver` и управляет всеми view; лаунчер лишь поднимает контекст и делегирует в сессию.
- Почему: чистое разделение ответственности (game loop vs view), соблюдение Observer, уменьшение связности, подготовка к Command/Decorator/тестам.

## Command Queue для ввода
- Было: InputHandler исполнял команды сразу при обработке событий, смешивая ввод с логикой и нарушая единый тик игрового цикла.
- Стало: InputHandler складывает команды в CommandQueue; GameSession исполняет очередь в начале тика перед AI/миром.
- Почему: чёткий порядок (input → AI → мир → рендер), детерминированность и соблюдение паттерна Command.

## Резервирование движения
- Было: проверка проходимости учитывала только статические препятствия; два танка могли “встретиться” в одном тайле за тик.
- Стало: MovementReservations делает снапшот занятых/зарезервированных тайлов; CombinedPassability использует его при проверках движения.
- Почему: исключить одновременное прохождение в одну клетку и отделить вычисление резервов от рендера/наблюдателей.

# Ключевые соблюдения SOLID

SRP: Разделены Model и View с hometask-2. Например, TankModel отвечает только за логику, а Tank — за рендеринг. В hometask-8 разделил God Object GameDesktopLauncher на GameSession (game loop), LevelGraphics (view), InputHandler (ввод).

OCP: Введены интерфейсы Renderable, Obstacle, BotStrategy. Новые типы объектов добавляются без изменения существующего кода. Например, добавил TmxLevelLoader без изменения GameSession.

LSP: HealthBarDecorator<Tank> можно использовать везде, где ожидается Renderable, без нарушения контракта.

ISP: Разделил GameObject (минимум методов) и специализированные интерфейсы Obstacle, HealthProvider, чтобы клиенты не зависели от ненужных методов.

DIP: Spring IoC инвертирует зависимости. GameDesktopLauncher получает BotStrategy (интерфейс), а не RandomStrategy (конкретный класс). Все зависимости инжектятся через конструктор."

# Ключевые шаблоны

**Command**
Используется для обработки ввода и AI через отложенное выполнение команд.
Позволяет отделить сбор действий от их исполнения и сделать игровой цикл детерминированным.
Ключевые классы: `CommandQueue`, `InputHandler`, `MoveCommand`, `ShootCommand`.
Добавлен в `hometask-7`, доработан и структурирован в `hometask-8`.

---

**Observer**
Используется для связи модели мира и визуального представления без прямых зависимостей.
Модель мира уведомляет наблюдателей о событиях, а view реагирует на них.
Ключевые классы: `WorldModel`, `LevelGraphics` (реализует `WorldObserver`).
Добавлен в `hometask-7`.

---

**Decorator**
Используется для добавления визуальных эффектов (полоски здоровья) без изменения базовых renderable-объектов.
Позволяет расширять функциональность, не нарушая OCP.
Ключевые классы: `HealthBarDecorator<T extends Renderable>`.
Добавлен в `hometask-7`, улучшен в `hometask-8` (сделан generic).

---

**Strategy**
Используется для инкапсуляции алгоритмов поведения AI и их взаимозаменяемости.
Позволяет подменять логику ботов без изменения игрового цикла.
Ключевые классы: `BotStrategy`, `RandomStrategy`, `HoldCourseStrategy`.
Добавлен в `hometask-5`.

---

**Factory**
Используется для централизованного и параметризуемого создания модели мира.
Скрывает сложность конструирования и упрощает конфигурацию и тестирование.
Ключевые классы: `WorldModelFactory` (в том числе параметры пуль).
Добавлен в `hometask-8`.

---

**Dependency Injection**
Используется для сборки и связывания всех компонентов приложения.
Позволяет избавиться от `new` в бизнес-коде и соблюдать DIP.
Ключевые элементы: Spring `@Configuration`, `@Bean`, `ObjectProvider`, `@Lazy`.
Добавлен в `hometask-8`.

---

**Repository**
Используется для загрузки уровней из разных источников.
Инкапсулирует доступ к данным и позволяет подменять способ загрузки.
Ключевые классы: `LevelLoader`, `FileLevelLoader`, `RandomLevelGenerator`, `TmxLevelLoader`.
Добавлен в `hometask-4`, расширен в `hometask-8`.

---

**Value Object**
Используется для представления неизменяемых данных без логики изменения состояния.
Объекты сравниваются по значению, а не по идентичности.
Ключевые классы: `Direction` (enum с `dx/dy/rotation`), `LevelData`, `ResourceCost`.
Используется с `hometask-1`.

---

**Facade**
Используется для упрощения работы с подсистемами игрового цикла.
Предоставляет единую точку управления без раскрытия внутренних деталей.
Ключевой класс: `GameSession` (координирует input, AI, мир и рендер).
Добавлен в `hometask-8`.

---

**Template Method**
Используется для задания общего алгоритма поведения сущностей с переопределяемыми шагами.
Позволяет вынести общий каркас логики в базовый класс.
Ключевые классы: `EntityModel` (абстрактный класс для `Tank`, `Tree`, `Bullet`).
Добавлен в `hometask-2`.

---

**Adapter**
Используется для изоляции доменной логики от LibGDX API.
Позволяет подменять источник ввода и упрощает тестирование.
Ключевой класс: `GdxInputSource` (обёртка над `Gdx.input`).
Добавлен в `hometask-2`.

---

**Snapshot**
Используется для фиксации состояния резервирования движения в неизменяемом виде.
Позволяет безопасно работать с состоянием без побочных эффектов.
Ключевые классы: `MovementReservations.Snapshot`.
Добавлен в `hometask-8`.

---

**Null Object**
Используется для безопасной обработки отсутствующих или отключённых компонентов.
Уменьшает количество проверок на `null` в основном коде.
Ключевой пример: `HealthBarsController` (проверка `!= null && isEnabled()`).
Используется в `hometask-7`.

