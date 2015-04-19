package io.github.alyphen.ld32;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static com.badlogic.gdx.Input.Keys.ENTER;
import static com.badlogic.gdx.graphics.Color.BLACK;
import static com.badlogic.gdx.graphics.Color.WHITE;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;

public class IntroScreen1 extends ScreenAdapter {

    private final LD32 game;

    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private Texture backgroundTexture;

    private Texture spaceShipSpriteSheet;
    private Animation spaceShip;

    private Texture alienShipSpriteSheet;
    private Animation alienShip;

    private float backgroundXOffset;
    private float stateTime;

    private boolean alienShipEntered;
    private float alienShipDist;

    private MessageQueue messageQueue;

    public IntroScreen1(final LD32 game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(WHITE);
        shapeRenderer = new ShapeRenderer();

        backgroundTexture = new Texture(Gdx.files.internal("intro_background.png"));
        spaceShipSpriteSheet = new Texture(Gdx.files.internal("intro_spaceship.png"));
        alienShipSpriteSheet = new Texture(Gdx.files.internal("intro_alienship.png"));

        spaceShip = new Animation(
                0.25F,
                new TextureRegion(spaceShipSpriteSheet, 0, 0, 32, 32),
                new TextureRegion(spaceShipSpriteSheet, 32, 0, 32, 32),
                new TextureRegion(spaceShipSpriteSheet, 64, 0, 32, 32),
                new TextureRegion(spaceShipSpriteSheet, 96, 0, 32, 32)
        );
        spaceShip.setPlayMode(LOOP);

        alienShip = new Animation(
                0.25F,
                new TextureRegion(alienShipSpriteSheet, 0, 0, 32, 32),
                new TextureRegion(alienShipSpriteSheet, 32, 0, 32, 32),
                new TextureRegion(alienShipSpriteSheet, 64, 0, 32, 32),
                new TextureRegion(alienShipSpriteSheet, 96, 0, 32, 32)
        );
        alienShip.setPlayMode(LOOP);

        messageQueue = new MessageQueue();
        messageQueue.queueMessage(new Message("55th century AD", WHITE, 1F));
        messageQueue.queueMessage(new Message("It is a time of peace and prosperity", WHITE, 1F));
        messageQueue.queueMessage(new Message("The human race is expanding across the universe after the death of the planet earth, and searching for new galaxies", WHITE, 1F));
        messageQueue.queueMessage(new Message("No new life forms have been found yet, but several settlements are growing and are able to support their population", WHITE, 1F));
        messageQueue.queueMessage(new Message("Scout ship Exion XII races across the galaxy in hopes of finding new land", WHITE, 1F));
        messageQueue.queueMessage(new Message("Suddenly...", WHITE, 1F));
        messageQueue.queueMessage(new MessageQueueCallback(5F) {
            @Override
            public void callback() {
                alienShipEntered = true;
                alienShipDist = 0F;
            }
        });
        messageQueue.queueMessage(new MessageQueueCallback(5F) {
            @Override
            public void callback() {
                game.setScreen(new IntroScreen2(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, -backgroundXOffset, 0);
        spriteBatch.draw(backgroundTexture, 800 - backgroundXOffset, 0);

        spriteBatch.draw(spaceShip.getKeyFrame(stateTime), 384, 316);

        spriteBatch.draw(alienShip.getKeyFrame(stateTime), 800 - alienShipDist, 332);

        spriteBatch.end();

        shapeRenderer.begin(Filled);
        shapeRenderer.setColor(BLACK);
        shapeRenderer.rect(0, 0, 800, 64);
        shapeRenderer.rect(0, 536, 800, 64);
        shapeRenderer.end();

        messageQueue.tick(delta);
        if (messageQueue.getMessage() != null) {
            spriteBatch.begin();
            font.setColor(messageQueue.getMessageColour());
            font.draw(spriteBatch, messageQueue.getMessage(), 32, 40);
            spriteBatch.end();
        }

        backgroundXOffset++;
        if (backgroundXOffset >= 800) backgroundXOffset = 0;

        if (alienShipEntered && alienShipDist < 384) alienShipDist += (delta * 300);

        stateTime += delta;

        if (Gdx.input.isKeyJustPressed(ENTER)) {
            game.setScreen(new IntroScreen2(game));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteBatch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        backgroundTexture.dispose();
        spaceShipSpriteSheet.dispose();
        alienShipSpriteSheet.dispose();
    }

    @Override
    public void show() {
        game.getIntroMusic().play();
    }

}
