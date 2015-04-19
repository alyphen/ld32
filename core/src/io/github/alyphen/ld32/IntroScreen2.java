package io.github.alyphen.ld32;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.Input.Keys.ENTER;
import static com.badlogic.gdx.graphics.Color.WHITE;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody;
import static java.lang.Math.max;

public class IntroScreen2 extends ScreenAdapter {

    private static final float TIME_STEP = 1/60F;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final int METRE_PIXELS = 16;

    private LD32 game;

    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private MessageQueue messageQueue;

    private Texture floorBlockSpriteSheet;
    private Animation floorBlock;

    private Texture human1SpriteSheet;
    private Animation human1Left;
    private Animation human1Right;

    private Texture human2SpriteSheet;
    private Animation human2Left;
    private Animation human2Right;

    private World world;
    private float accumulator;

    public IntroScreen2(final LD32 game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        camera = new OrthographicCamera(800, 600);
        camera.setToOrtho(false);
        floorBlockSpriteSheet = new Texture(Gdx.files.internal("block_flooring.png"));
        floorBlock = new Animation(0F, new TextureRegion(floorBlockSpriteSheet, 0, 0, 16, 16));
        human1SpriteSheet = new Texture(Gdx.files.internal("human1.png"));
        human1Left = new Animation(
                0.5F,
                new TextureRegion(human1SpriteSheet, 0, 0, 16, 32),
                new TextureRegion(human1SpriteSheet, 16, 0, 16, 32),
                new TextureRegion(human1SpriteSheet, 32, 0, 16, 32),
                new TextureRegion(human1SpriteSheet, 48, 0, 16, 32)
        );
        human1Left.setPlayMode(LOOP);
        human1Right = new Animation(
                0.5F,
                new TextureRegion(human1SpriteSheet, 0, 32, 16, 32),
                new TextureRegion(human1SpriteSheet, 16, 32, 16, 32),
                new TextureRegion(human1SpriteSheet, 32, 32, 16, 32),
                new TextureRegion(human1SpriteSheet, 48, 32, 16, 32)
        );
        human1Right.setPlayMode(LOOP);
        human2SpriteSheet = new Texture(Gdx.files.internal("human2.png"));
        human2Left = new Animation(
                0.5F,
                new TextureRegion(human2SpriteSheet, 0, 0, 16, 32),
                new TextureRegion(human2SpriteSheet, 16, 0, 16, 32),
                new TextureRegion(human2SpriteSheet, 32, 0, 16, 32),
                new TextureRegion(human2SpriteSheet, 48, 0, 16, 32)
        );
        human2Left.setPlayMode(LOOP);
        human2Right = new Animation(
                0.5F,
                new TextureRegion(human2SpriteSheet, 0, 32, 16, 32),
                new TextureRegion(human2SpriteSheet, 16, 32, 16, 32),
                new TextureRegion(human2SpriteSheet, 32, 32, 16, 32),
                new TextureRegion(human2SpriteSheet, 48, 32, 16, 32)
        );
        human2Right.setPlayMode(LOOP);
        Box2D.init();
        world = new World(new Vector2(0F, -9.81F), true);
        for (float x = 0.5F; x < camera.viewportWidth / METRE_PIXELS; x += 1F) {
            createBlock(x, 0.5F);
        }
        createHuman1(5, 1.5F);
        createHuman2(8, 1.5F);
        messageQueue = new MessageQueue();
        messageQueue.queueMessage(new Message("Human 1: We're being boarded!", WHITE, 1F));
        messageQueue.queueMessage(new Message("Human 2: What? I didn't think there was anything out there!", WHITE, 1F));
        messageQueue.queueMessage(new Message("Human 1: Apparently there was! Now, how are we going to defend ourselves?", WHITE, 1F));
        messageQueue.queueMessage(new Message("Human 2: We'll just have to see what we can find!", WHITE, 1F));
        messageQueue.queueMessage(new MessageQueueCallback(1F) {
            @Override
            public void callback() {
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    public void createBlock(float x, float y) {
        BodyDef blockDef = new BodyDef();
        blockDef.type = StaticBody;
        blockDef.position.set(x, y);
        Body block = world.createBody(blockDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 0.5F);
        block.createFixture(shape, 0F);
        shape.dispose();
        block.setUserData(new ObjectData(floorBlock));
    }

    public void createHuman1(float x, float y) {
        BodyDef humanDef = new BodyDef();
        humanDef.type = DynamicBody;
        humanDef.position.set(x, y);
        Body human = world.createBody(humanDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 1F);
        human.createFixture(shape, 1F);
        shape.dispose();
        human.setUserData(new ObjectData(human1Right));
        human.setFixedRotation(true);
    }

    public void createHuman2(float x, float y) {
        BodyDef humanDef = new BodyDef();
        humanDef.type = DynamicBody;
        humanDef.position.set(x, y);
        Body human = world.createBody(humanDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 1F);
        human.createFixture(shape, 1F);
        shape.dispose();
        human.setUserData(new ObjectData(human2Left));
        human.setFixedRotation(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        Array<Body> bodies = new Array<>(world.getBodyCount());
        world.getBodies(bodies);
        for (Body body : bodies) {
            if (body.getUserData() instanceof ObjectData) {
                ObjectData objectData = (ObjectData) body.getUserData();
                objectData.tick(delta);
                TextureRegion frame = objectData.getCurrentFrame();
                spriteBatch.draw(frame, (body.getPosition().x * METRE_PIXELS) - (frame.getRegionWidth() / 2), (body.getPosition().y * METRE_PIXELS) - (frame.getRegionHeight() / 2));
            }
        }
        spriteBatch.end();
        float frameTime = max(delta, TIME_STEP);
        accumulator += frameTime;
        while (accumulator > TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }

        messageQueue.tick(delta);
        if (messageQueue.getMessage() != null) {
            spriteBatch.begin();
            font.setColor(messageQueue.getMessageColour());
            font.draw(spriteBatch, messageQueue.getMessage(), 32, 560);
            spriteBatch.end();
        }

        if (Gdx.input.isKeyJustPressed(ENTER)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
        human1SpriteSheet.dispose();
        human2SpriteSheet.dispose();
        floorBlockSpriteSheet.dispose();
        world.dispose();
    }
}
