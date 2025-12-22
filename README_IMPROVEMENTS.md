# Improvements Log

## Spring IoC and Lifecycle (block 1)
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
