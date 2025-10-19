// Разделены модели и графика, обработка ввода вынесена в InputHandler со стрельбой
package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeObstacleModel;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {

    private static final float MOVEMENT_SPEED = 0.4f;

    private Batch batch;
    private GameField field;
    private Tank player;
    private TreeObstacle tree;
    private InputHandler inputHandler;

    @Override
    public void create() {
        batch = new SpriteBatch();
        field = new GameField(batch);

        TankModel tankModel = new TankModel(MOVEMENT_SPEED);
        tankModel.setPosition(new GridPoint2(1, 1));
        player = new Tank(new Texture("images/tank_blue.png"), tankModel);
        player.align(field.movement());

        TreeObstacleModel treeModel = new TreeObstacleModel();
        treeModel.setPosition(new GridPoint2(1, 3));
        tree = new TreeObstacle(new Texture("images/greenTree.png"), treeModel);
        tree.align(field.movement());

        inputHandler = new InputHandler(tankModel, treeModel);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        inputHandler.handle();
        player.update(field.movement(), deltaTime);

        field.render();

        batch.begin();
        player.render(batch);
        tree.render(batch);
        batch.end();
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
        tree.dispose();
        player.dispose();
        field.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
