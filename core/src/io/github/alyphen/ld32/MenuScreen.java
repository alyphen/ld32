package io.github.alyphen.ld32;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.badlogic.gdx.Input.Buttons.LEFT;
import static com.badlogic.gdx.Input.Keys.ENTER;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class MenuScreen extends ScreenAdapter {

    private final LD32 game;
    private SpriteBatch spriteBatch;
    private Texture startButton;

    public MenuScreen(LD32 game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
        startButton = new Texture(Gdx.files.internal("start_button.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(startButton, 336, 332);
        spriteBatch.end();
        if (Gdx.input.isButtonPressed(LEFT) || Gdx.input.isKeyJustPressed(ENTER)) {
            game.setScreen(new GameScreen(game));
        }
    }
}
