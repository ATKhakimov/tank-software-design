package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.level.LevelData;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.WorldModel;
import ru.mipt.bit.platformer.model.WorldObserver;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.config.WorldModelFactory;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TreeObstacleModel;
import ru.mipt.bit.platformer.model.TankModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener, WorldObserver {

    private static final float MOVEMENT_SPEED = 0.4f;

    private final ObjectProvider<Batch> batchProvider;
    private final ObjectProvider<GameField> fieldProvider;
    private final ObjectProvider<LevelLoader> levelLoaderProvider;
    private final ObjectProvider<Texture> tankTextureProvider;
    private final ObjectProvider<Texture> treeTextureProvider;
    private final ObjectProvider<Texture> pixelTextureProvider;
    private Batch batch;
    private GameField field;
    private LevelLoader levelLoader;
    private Texture tankTexture;
    private Texture treeTexture;
    private Texture pixelTexture;
    private final int botsCount;
    private Tank player;
    private Renderable playerWithHealth;
    private final List<Renderable> obstacles = new ArrayList<>();
    private final List<Tank> aiTanks = new ArrayList<>();
    private final List<Renderable> aiTanksWithHealth = new ArrayList<>();
    private final List<Renderable> bullets = new ArrayList<>();
    private final List<TankModel> aiModels = new ArrayList<>();
    private InputHandler inputHandler;
    private AIHandler aiHandler;
    private MovementRules movementRules;
    private final HealthBarsController healthBarsController;
    private WorldModel world;
    private final WorldModelFactory worldFactory;
    private final BotStrategy botStrategy;

    public GameDesktopLauncher(BotStrategy botStrategy, HealthBarsController healthBarsController, WorldModelFactory worldFactory,
                               ObjectProvider<Batch> batchProvider,
                               ObjectProvider<GameField> fieldProvider,
                               ObjectProvider<LevelLoader> levelLoaderProvider,
                               @Qualifier("tankTexture") ObjectProvider<Texture> tankTextureProvider,
                               @Qualifier("treeTexture") ObjectProvider<Texture> treeTextureProvider,
                               @Qualifier("pixelTexture") ObjectProvider<Texture> pixelTextureProvider,
                               int botsCount) {
        this.botStrategy = botStrategy;
        this.healthBarsController = healthBarsController;
        this.worldFactory = worldFactory;
        this.batchProvider = batchProvider;
        this.fieldProvider = fieldProvider;
        this.levelLoaderProvider = levelLoaderProvider;
        this.tankTextureProvider = tankTextureProvider;
        this.treeTextureProvider = treeTextureProvider;
        this.pixelTextureProvider = pixelTextureProvider;
        this.botsCount = botsCount;
    }

    @Override
    public void create() {
        batch = batchProvider.getObject();
        field = fieldProvider.getObject();
        levelLoader = levelLoaderProvider.getObject();
        tankTexture = tankTextureProvider.getObject();
        treeTexture = treeTextureProvider.getObject();
        pixelTexture = pixelTextureProvider.getObject();

        LevelData data = levelLoader.load();

        world = worldFactory.create(field.widthInTiles(), field.heightInTiles());
        world.addObserver(this);
        TankModel tankModel = new TankModel(MOVEMENT_SPEED);
        tankModel.setPosition(new GridPoint2(data.getPlayerStart().x, data.getPlayerStart().y));
        world.addTank(tankModel);

        Tank tank = new Tank(tankTexture, tankModel);
        tank.align(field.movement());
        player = tank;
        playerWithHealth = new HealthBarTank(player, tankModel, pixelTexture);
        playerWithHealth.align(field.movement());

        List<Obstacle> obstacleModels = new ArrayList<>();
        for (GridPoint2 pos : data.getTreePositions()) {
            TreeObstacleModel m = new TreeObstacleModel();
            m.setPosition(new GridPoint2(pos.x, pos.y));
            obstacleModels.add(m);
            world.addObstacle(m);
        }

        
        int w = field.widthInTiles();
        int h = field.heightInTiles();
        Set<GridPoint2> forbidden = new HashSet<>();
        forbidden.add(new GridPoint2(tankModel.getCoordinates()));
        for (Obstacle o : obstacleModels) {
            forbidden.add(new GridPoint2(o.getCoordinates()));
        }
        List<GridPoint2> free = new ArrayList<>();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                GridPoint2 p = new GridPoint2(x, y);
                if (!forbidden.contains(p)) free.add(p);
            }
        }
        int aiCount = Math.min(botsCount, free.size());
        Random rnd = new Random();
        for (int i = 0; i < aiCount; i++) {
            int idx = rnd.nextInt(free.size());
            GridPoint2 p = free.remove(idx);
            TankModel ai = new TankModel(MOVEMENT_SPEED);
            ai.setPosition(new GridPoint2(p));
            world.addTank(ai);
            aiModels.add(ai);
        }

        movementRules = new MovementRules(w, h, obstacleModels);
        inputHandler = new InputHandler(tankModel, movementRules, healthBarsController);
        inputHandler.setShooter(world);
        aiHandler = new AIHandler(movementRules, aiModels, botStrategy, world);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        Set<GridPoint2> occupied = computeOccupied();
        Set<GridPoint2> reserved = computeReserved();
        movementRules.setOccupied(occupied, reserved);

        inputHandler.handle();
        aiHandler.handle();

        player.update(field.movement(), deltaTime);
        for (Tank t : aiTanks) t.update(field.movement(), deltaTime);
        world.tick(deltaTime);
        for (Renderable b : bullets) {
            if (b instanceof BulletRenderable) {
                ((BulletRenderable) b).update(field.movement());
            }
        }

        field.render();

        batch.begin();
        if (healthBarsController != null && healthBarsController.isEnabled() && playerWithHealth != null) playerWithHealth.render(batch); else player.render(batch);
        for (Renderable r : obstacles) {
            r.render(batch);
        }
        if (healthBarsController != null && healthBarsController.isEnabled()) {
            for (Renderable t : aiTanksWithHealth) {
                t.render(batch);
            }
        } else {
            for (Tank t : aiTanks) {
                t.render(batch);
            }
        }
        for (Renderable b : bullets) b.render(batch);
        batch.end();
    }

    private Set<GridPoint2> computeOccupied() {
        Set<GridPoint2> occ = new HashSet<>();
        addCurrentCells(occ, player);
        for (Tank t : aiTanks) addCurrentCells(occ, t);
        return occ;
    }

    private Set<GridPoint2> computeReserved() {
        Set<GridPoint2> res = new HashSet<>();
        addMovingReservations(res, player);
        for (Tank t : aiTanks) addMovingReservations(res, t);
        return res;
    }

    private void addCurrentCells(Set<GridPoint2> set, Tank t) {
        GridPoint2 from = t.getModel().getCoordinates();
        set.add(new GridPoint2(from));
    }

    private void addMovingReservations(Set<GridPoint2> set, Tank t) {
        if (!t.getModel().isReady()) {
            set.add(new GridPoint2(t.getModel().getCoordinates()));
            set.add(new GridPoint2(t.getModel().getDestination()));
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        bullets.clear();
        aiTanksWithHealth.clear();
        aiTanks.clear();
        obstacles.clear();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        try (org.springframework.context.annotation.AnnotationConfigApplicationContext ctx = new org.springframework.context.annotation.AnnotationConfigApplicationContext(ru.mipt.bit.platformer.config.GameSessionConfiguration.class)) {
            GameDesktopLauncher launcher = ctx.getBean(GameDesktopLauncher.class);
            new Lwjgl3Application(launcher, config);
        }
    }

    @Override
    public void objectAdded(GameObject object) {
        if (object instanceof TankModel) {
            TankModel tm = (TankModel) object;
            if (player != null && player.getModel() == tm) {
                return;
            }
            for (Tank existing : aiTanks) {
                if (existing.getModel() == tm) {
                    return;
                }
            }
            Tank tank = new Tank(tankTexture, tm);
            tank.align(field.movement());
            if (player == null) {
                player = tank;
                playerWithHealth = new HealthBarTank(player, tm, pixelTexture);
                playerWithHealth.align(field.movement());
            } else {
                aiTanks.add(tank);
                Renderable decorated = new HealthBarTank(tank, tm, pixelTexture);
                decorated.align(field.movement());
                aiTanksWithHealth.add(decorated);
            }
        } else if (object instanceof Obstacle) {
            Obstacle o = (Obstacle) object;
            if (o instanceof TreeObstacleModel) {
                TreeObstacleModel tm = (TreeObstacleModel) o;
                Renderable r = new TreeObstacle(treeTexture, tm);
                r.align(field.movement());
                obstacles.add(r);
            }
        } else if (object instanceof BulletModel) {
            BulletModel bm = (BulletModel) object;
            BulletRenderable br = new BulletRenderable(bm, pixelTexture);
            br.align(field.movement());
            bullets.add(br);
        }
    }

    @Override
    public void objectRemoved(GameObject object) {
        if (object instanceof TankModel) {
            TankModel tm = (TankModel) object;
            if (player != null && player.getModel() == tm) {
                player = null;
                playerWithHealth = null;
            } else {
                for (int i = 0; i < aiTanks.size(); i++) {
                    if (aiTanks.get(i).getModel() == tm) {
                        aiTanks.remove(i);
                        aiTanksWithHealth.remove(i);
                        break;
                    }
                }
            }
        } else if (object instanceof Obstacle) {
            for (int i = 0; i < obstacles.size(); i++) {
                Renderable r = obstacles.get(i);
                if (r instanceof TreeObstacle) {
                    // no direct link; skip removal for simplicity
                }
            }
        } else if (object instanceof BulletModel) {
            for (int i = 0; i < bullets.size(); i++) {
                Renderable r = bullets.get(i);
                if (r instanceof BulletRenderable) {
                    BulletRenderable br = (BulletRenderable) r;
                    if (br.getModel() == object) {
                        bullets.remove(i);
                        break;
                    }
                }
            }
        }
    }
}
