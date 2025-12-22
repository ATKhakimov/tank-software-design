package ru.mipt.bit.platformer.config;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.mipt.bit.platformer.view.GameField;

@Configuration
public class RenderingConfiguration {
    @Bean(destroyMethod = "dispose")
    @Lazy
    public Batch batch() {
        return new SpriteBatch();
    }

    @Bean(destroyMethod = "dispose")
    @Lazy
    public GameField gameField(Batch batch) {
        return new GameField(batch);
    }

    @Bean(destroyMethod = "dispose")
    @Lazy
    public Texture tankTexture() {
        return new Texture("images/tank_blue.png");
    }

    @Bean(destroyMethod = "dispose")
    @Lazy
    public Texture treeTexture() {
        return new Texture("images/greenTree.png");
    }

    @Bean(destroyMethod = "dispose")
    @Lazy
    public Texture pixelTexture() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1f, 1f, 1f, 1f);
        pm.fillRectangle(0, 0, 1, 1);
        Texture texture = new Texture(pm);
        pm.dispose();
        return texture;
    }
}
