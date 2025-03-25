package com.Dan.wasteland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen implements Screen {
    private Stage stage;
    private Player player;
    private SpriteBatch batch;
    private MapManager mapManager;
    private BulletPool bulletPool;
    private MapRenderer mapRenderer;

    private boolean introFinished = false; // Track if intro is done
    private float introTimer = 0; // Timer for the intro message
    private Label introLabel;
    private OrthographicCamera camera;  // Camera for following the player

    public GameScreen() {
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        bulletPool = new BulletPool(10);
        player = new Player("characters/player.png"); // - Texture of my character!

        // Set up camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 768);
        // camera.rotate(-45);  // Rotate camera for isometric view
        camera.position.set(400, 300, 0);
        camera.update();

        Gdx.input.setInputProcessor(stage);

        // Initialize MapManager
        mapManager = new MapManager("maps/256x128.tmx");
        mapRenderer = mapManager.getMapRenderer();
        //mapManager.loadObjects();

        // UI Setup
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        introLabel  = new Label("Welcome to the Wasteland!", skin);
        introLabel.setAlignment(Align.center);

        Table table = new Table();
        table.setFillParent(true);
        table.add(introLabel).expand().center();// Position at the top
        stage.addActor(table);

    }
    @Override
    public void render(float delta) {

        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!introFinished) {

            introTimer += delta;
            // Skip intro when 3 seconds pass or player presses a key
            if (introTimer > 3 || Gdx.input.isTouched()) {
                introFinished = true;
                stage.clear(); // Remove the message
            }
            stage.act(delta);
            stage.draw();

        } else {

            // Update camera position to follow player smoothly
            Vector3 target = new Vector3(player.getPosition().x, player.getPosition().y, 0);
            camera.position.lerp(target, 0.1f);  // Smooth transition
            camera.update();

            // Check if the player presses the "shoot" key (e.g., SPACE)
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                shoot();
            }

            mapManager.render(camera, batch);


            // Render player
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            player.update(delta);
            player.draw(batch);



            bulletPool.updateAll(delta);
            bulletPool.drawAll(batch);
            batch.end();
        }

        // Render UI
        if(introFinished) {
            stage.act(delta);
            stage.draw();
        }
    }













    public void shoot() {

        Vector2 playerPosition = player.getPosition();
        float bulletSpeed = 3000f;

        // Get the player's facing direction (assuming the player has a "direction" Vector2)
        Vector2 bulletDirection = new Vector2(player.getDirection()).nor(); // Normalize to ensure consistent speed

        // Calculate bullet's initial position to be right in front of the player
        float bulletStartX = playerPosition.x + player.getWidth() * 0.5f * bulletDirection.x;
        float bulletStartY = playerPosition.y + player.getHeight() * 0.5f * bulletDirection.y;

        // Create a bullet
        bulletPool.getBullet(
            bulletStartX,
            bulletStartY,
            bulletDirection.x * bulletSpeed,
            bulletDirection.y * bulletSpeed
        );
        
    }






    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }




    @Override
    public void show() {}
    @Override
    public void hide() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        player.dispose();
        mapManager.dispose();
    }
}
