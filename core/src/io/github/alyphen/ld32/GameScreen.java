package io.github.alyphen.ld32;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.graphics.Color.RED;
import static com.badlogic.gdx.graphics.Color.WHITE;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class GameScreen extends ScreenAdapter {

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

    private Texture ladderSpriteSheet;
    private Animation ladderImage;

    private Texture human1SpriteSheet;
    private Animation human1Left;
    private Animation human1Right;

    private ObjectData playerRightData;
    private ObjectData playerLeftData;

    private Body player;

    private Texture human2SpriteSheet;
    private Animation human2Left;
    private Animation human2Right;

    private Texture weaponSpriteSheet;
    private Animation toasterAnimation;
    private Animation toastAnimation;

    private Texture alien1SpriteSheet;
    private Animation alien1Left;
    private Animation alien1Right;
    private ObjectData alien1LeftData;
    private ObjectData alien1RightData;

    private World world;
    private float accumulator;

    private int numContacts;

    private Sound toasterSound = Gdx.audio.newSound(Gdx.files.internal("toaster.wav"));

    public GameScreen(LD32 game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        camera = new OrthographicCamera(800, 600);
        camera.setToOrtho(false);
        floorBlockSpriteSheet = new Texture(Gdx.files.internal("block_flooring.png"));
        floorBlock = new Animation(0F, new TextureRegion(floorBlockSpriteSheet, 0, 0, 16, 16));
        ladderSpriteSheet = new Texture(Gdx.files.internal("ladder.png"));
        ladderImage = new Animation(0F, new TextureRegion(ladderSpriteSheet, 0, 0, 16, 16));
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
        weaponSpriteSheet = new Texture(Gdx.files.internal("weapons.png"));
        toasterAnimation = new Animation(
                0F,
                new TextureRegion(weaponSpriteSheet, 0, 0, 16, 16)
        );
        toastAnimation = new Animation(
                0F,
                new TextureRegion(weaponSpriteSheet, 16, 0, 16, 16)
        );
        alien1SpriteSheet = new Texture(Gdx.files.internal("alien1.png"));
        alien1Left = new Animation(
                0.5F,
                new TextureRegion(alien1SpriteSheet, 0, 0, 16, 32),
                new TextureRegion(alien1SpriteSheet, 16, 0, 16, 32),
                new TextureRegion(alien1SpriteSheet, 32, 0, 16, 32),
                new TextureRegion(alien1SpriteSheet, 48, 0, 16, 32)
        );
        alien1Left.setPlayMode(LOOP);
        alien1Right = new Animation(
                0.5F,
                new TextureRegion(alien1SpriteSheet, 0, 32, 16, 32),
                new TextureRegion(alien1SpriteSheet, 16, 32, 16, 32),
                new TextureRegion(alien1SpriteSheet, 32, 32, 16, 32),
                new TextureRegion(alien1SpriteSheet, 48, 32, 16, 32)
        );
        alien1Right.setPlayMode(LOOP);
        alien1LeftData = new ObjectData(alien1Left);
        alien1RightData = new ObjectData(alien1Right);
        Box2D.init();
        world = new World(new Vector2(0F, -9.81F), true);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if ((contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("footsense")) || (contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("footsense"))) {
                    numContacts++;
                }
            }

            @Override
            public void endContact(Contact contact) {
                if ((contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("footsense")) || (contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("footsense"))) {
                    numContacts--;
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        loadLevel("room1");
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                shootToast();
                return true;
            }
        });
        messageQueue = new MessageQueue();
        messageQueue.queueMessage(new Message("Push the aliens out of the spaceship by launching toast from your toaster.", WHITE, 1F));
        messageQueue.queueMessage(new Message("You can launch toast from your toaster by clicking, and move with WASD or arrows.", WHITE, 1F));
        messageQueue.queueMessage(new Message("You win when all the enemies are pushed off.", WHITE, 1F));
        messageQueue.queueMessage(new Message("Except there really isn't anything to detect this, so all you get is moral satisfaction.", WHITE, 1F));
        messageQueue.queueMessage(new Message("Okay, enjoy the music, apologies for the lack of content this time.", WHITE, 1F));
        messageQueue.queueMessage(new Message("<3", RED, 3F));
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
                if ((objectData.getAnimation() == alien1Left || objectData.getAnimation() == alien1Right) && abs(body.getLinearVelocity().x) < 5) {
                    if (body.getPosition().x > player.getPosition().x) {
                        body.applyLinearImpulse(new Vector2(-2F, 0F), body.getPosition(), true);
                        body.setUserData(alien1LeftData);
                    } else {
                        body.applyLinearImpulse(new Vector2(2F, 0F), body.getPosition(), true);
                        body.setUserData(alien1RightData);
                    }
                    if (body.getLinearVelocity().y < 0) body.applyLinearImpulse(new Vector2(0F, 9.81F), body.getPosition(), true);
                }
                TextureRegion frame = objectData.getCurrentFrame();
                spriteBatch.draw(frame, (body.getPosition().x * METRE_PIXELS) - (frame.getRegionWidth() / 2), (body.getPosition().y * METRE_PIXELS) - (frame.getRegionHeight() / 2));
            }
        }
        spriteBatch.draw(toasterAnimation.getKeyFrame(0F), Gdx.input.getX() < 400 ? camera.position.x - 24 : camera.position.x + 8, camera.position.y);
        spriteBatch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setUserData(playerLeftData);
            if (player.getLinearVelocity().x > -5) {
                player.applyLinearImpulse(-1F, 0F, player.getPosition().x, player.getPosition().y, true);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setUserData(playerRightData);
            if (player.getLinearVelocity().x < 5) {
                player.applyLinearImpulse(1F, 0F, player.getPosition().x, player.getPosition().y, true);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (player.getLinearVelocity().y < 5 && numContacts > 0) {
                player.applyLinearImpulse(0F, 20F, player.getPosition().x, player.getPosition().y, true);
            }
        }

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
            font.draw(spriteBatch, messageQueue.getMessage(), camera.position.x - 368, camera.position.y + 268);
            spriteBatch.end();
        }
        camera.position.set(player.getPosition().x * METRE_PIXELS, player.getPosition().y * METRE_PIXELS, 0);
        if (player.getPosition().y < 0) {
            game.setScreen(new MenuScreen(game));
            game.getGameMusic().stop();
            game.getIntroMusic().play();
        }
    }

    private void loadLevel(String levelName) {
        String fileContents = Gdx.files.internal(levelName + ".csv").readString();
        String[] rows = fileContents.split("\n");
        for(int i = 0; i < rows.length / 2; i++) {
            String temp = rows[i];
            rows[i] = rows[rows.length - i - 1];
            rows[rows.length - i - 1] = temp;
        }
        for (int rowi = 0; rowi < rows.length; rowi++) {
            String row = rows[rowi];
            String[] cols = row.split(",");
            for (int coli = 0; coli < cols.length; coli++) {
                String col = cols[coli];
                switch (col) {
                    case "F":
                        createFloor(coli, rowi);
                        break;
                    case "L":
                        createLadder(coli, rowi);
                        break;
                    case "P":
                        createPlayer(coli, rowi);
                        break;
                    case "A":
                        createAlien(coli, rowi);
                        break;
                }
            }
        }

    }

    private void createFloor(int row, int col) {
        BodyDef blockDef = new BodyDef();
        blockDef.type = StaticBody;
        blockDef.position.set(row, col);
        Body block = world.createBody(blockDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 0.5F);
        block.createFixture(shape, 1F);
        shape.dispose();
        block.setUserData(new ObjectData(floorBlock));
    }

    private void createLadder(int row, int col) {
        BodyDef ladderDef = new BodyDef();
        ladderDef.type = StaticBody;
        ladderDef.position.set(row, col);
        Body ladder = world.createBody(ladderDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 0.5F);
        FixtureDef ladderFixtureDef = new FixtureDef();
        ladderFixtureDef.shape = shape;
        ladderFixtureDef.isSensor = true;
        ladder.createFixture(ladderFixtureDef);
        ladder.setUserData(new ObjectData(ladderImage));
    }

    private void createPlayer(int row, int col) {
        BodyDef playerDef = new BodyDef();
        playerDef.type = DynamicBody;
        playerDef.position.set(row, col);
        final Body player = world.createBody(playerDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 1F);
        player.createFixture(shape, 1F);
        shape.dispose();
        PolygonShape footSensorShape = new PolygonShape();
        footSensorShape.setAsBox(0.3F, 0.3F, new Vector2(0F, -2F), 0);
        FixtureDef footSensorDef = new FixtureDef();
        footSensorDef.shape = footSensorShape;
        footSensorDef.isSensor = true;
        Fixture footSensor = player.createFixture(footSensorDef);
        footSensor.setUserData("footsense");
        playerRightData = new ObjectData(human1Right);
        playerLeftData = new ObjectData(human1Left);
        player.setUserData(playerRightData);
        player.setFixedRotation(true);
        this.player = player;
    }

    private void createAlien(int row, int col) {
        BodyDef alienDef = new BodyDef();
        alienDef.type = DynamicBody;
        alienDef.position.set(row, col);
        final Body alien = world.createBody(alienDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 1F);
        alien.createFixture(shape, 1F);
        shape.dispose();
        alien.setUserData(alien1LeftData);
        alien.setFixedRotation(true);
    }

    private void shootToast() {
        BodyDef toastDef = new BodyDef();
        toastDef.type = DynamicBody;
        if (Gdx.input.getX() < 400)
            toastDef.position.set(player.getPosition().x - 0.5F, player.getPosition().y);
        else
            toastDef.position.set(player.getPosition().x + 0.5F, player.getPosition().y);
        Body toast = world.createBody(toastDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5F, 0.5F);
        toast.createFixture(shape, 0.5F);
        shape.dispose();
        toast.setUserData(new ObjectData(toastAnimation));
        toast.applyLinearImpulse(new Vector2(Gdx.input.getX() < 400 ? -20 : 20, 0), player.getPosition(), true);
        toasterSound.play();
    }

    @Override
    public void show() {
        game.getIntroMusic().stop();
        game.getGameMusic().setLooping(true);
        game.getGameMusic().play();
    }
}
