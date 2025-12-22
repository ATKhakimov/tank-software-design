# Нарушения SOLID (план)


## src/main/java/ru/mipt/bit/platformer/GameSession.java
- SRP: класс одновременно загружает уровень, создает мир, спавнит танки/препятствия, настраивает ввод и AI, управляет кадром и жизненным циклом — много причин для изменений.
- DIP: зависит от конкретных реализаций (`LevelGraphics`, `MovementRules`, `InputHandler`, `AIHandler`, `TankModel`) и сам их создает вместо абстракций/фабрик.
- OCP: логика спавна, скорость, правила размещения и состав действий «зашиты» в класс; расширение требует правки.
- Смешение слоев (DIP/арх. границы): размеры мира берутся из `levelGraphics.getField()`, то есть модель зависит от рендера; при других `LevelLoader` размеры могут не совпасть.

## src/main/java/ru/mipt/bit/platformer/InputHandler.java
- SRP: обработка ввода, маппинг клавиш, создание `MovementRules` и UI‑команда (toggle health bars) собраны в одном классе.
- DIP: прямая зависимость от `Gdx.input` (статик) и конкретных команд/моделей; сложно подменять источник ввода и тестировать.
- OCP: добавление новых команд/переназначение клавиш требует правки `InputHandler` и его внутренних классов.

## src/main/java/ru/mipt/bit/platformer/LevelGraphics.java
- SRP: наблюдатель мира, фабрика вью‑объектов, их хранение и рендеринг в одном классе.
- OCP: ветвление по `instanceof` в `objectAdded/objectRemoved` — добавление нового типа объекта требует правок.
- DIP: напрямую создает конкретные `Tank`, `TreeObstacle`, `BulletRenderable`, `HealthBarDecorator`, что жестко связывает графику с деталями моделей.

## src/main/java/ru/mipt/bit/platformer/AIHandler.java
- SRP: принимает решения ИИ, выполняет действия (движение/стрельба), а также содержит вероятность стрельбы — несколько причин для изменения.
- OCP/DIP: проверка `strategy instanceof HoldCourseStrategy` и каст; добавление новых стратегий с дополнительным поведением требует правки.

## src/main/java/ru/mipt/bit/platformer/GameField.java
- SRP: загрузка карты, создание рендера, логика движения по тайлам и доступ к размерам поля в одном классе.
- DIP/OCP: жесткая зависимость от `TmxMapLoader` и ресурса `"level.tmx"`; смена источника/формата требует правки класса.

## src/main/java/ru/mipt/bit/platformer/model/WorldModel.java
- SRP: хранение мира, бизнес‑правила стрельбы/урона, симуляция пуль и уведомления наблюдателей объединены в одном классе.
- OCP: добавление новых типов оружия/правил столкновений требует модификации `WorldModel`.

## src/main/java/ru/mipt/bit/platformer/model/BulletModel.java
- SRP: хранит состояние пули и одновременно содержит правила взаимодействия с миром.
- DIP: зависит от конкретного `WorldModel` для проверки коллизий и урона вместо абстракции мира.

## Внесенные исправления
- `src/main/java/ru/mipt/bit/platformer/GameSession.java`: размеры мира теперь берутся из `LevelData`, а не из `LevelGraphics` (убрали зависимость логики от рендера).
- `src/main/java/ru/mipt/bit/platformer/level/LevelData.java`: добавлены `width/height` и `setSize`.
- `src/main/java/ru/mipt/bit/platformer/level/FileLevelLoader.java`: заполняет размеры уровня из текстовой карты.
- `src/main/java/ru/mipt/bit/platformer/level/RandomLevelGenerator.java`: выставляет размеры и использует их в генерации.
- `src/main/java/ru/mipt/bit/platformer/level/TmxLevelLoader.java`: читает размеры карты из TMX и переносит в `LevelData`.
 - `src/main/java/ru/mipt/bit/platformer/InputHandler.java`: выделен источник ввода `InputSource`, обработчик больше не зависит напрямую от `Gdx.input`; команды ввода оформлены через публичный интерфейс `InputCommand`, добавлена возможность инъекции/расширения набора команд (OCP). Сохранен совместимый конструктор, создающий дефолтные биндинги. Удалены конструкторы, создающие `MovementRules` внутри (DIP/SRP).
 - `src/main/java/ru/mipt/bit/platformer/InputSource.java`: новая абстракция источника ввода (DIP).
 - `src/main/java/ru/mipt/bit/platformer/GdxInputSource.java`: адаптер к `Gdx.input`.
