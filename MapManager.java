package com.Dan.wasteland;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;


public class MapManager {

    private TiledMap map;
    private TiledMapTileLayer terrainLayer;
    private int [] decorationLayerIndices;
    private IsometricTiledMapRenderer mapRenderer;
    private List<GameObject> gameObjects;

    

    public IsometricTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public MapManager(String mapPath) {

        try {
            System.out.println("Loading map: " + mapPath);
            map = new TmxMapLoader().load(mapPath);
            gameObjects = new ArrayList<>();
            createObjects();
            loadObjects();
            if (map == null) {
                System.out.println("ERROR: Map failed to load!");
            } else {
                System.out.println("Map loaded successfully.");
            }

            mapRenderer = new IsometricTiledMapRenderer(map);

            fixTextureBleeding();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fixTextureBleeding() {
        for (TiledMapTileLayer layer : map.getLayers().getByType(TiledMapTileLayer.class)) {
            for (int y = 0; y < layer.getHeight(); y++) {
                for (int x = 0; x < layer.getWidth(); x++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null && cell.getTile() != null) {
                        TextureRegion region = cell.getTile().getTextureRegion();
                        Texture texture = region.getTexture();
                        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
                    }
                }
            }
        }
    }

    public void createObjects () {
        System.out.println("Creating objects");
        MapLayers mapLayers = map.getLayers();
        terrainLayer = ( TiledMapTileLayer ) mapLayers.get("Ground");
        decorationLayerIndices = new int[] {
                mapLayers.getIndex("crates")
        };
    }

    private void loadObjects () {
        MapLayer objectLayer = map.getLayers().get("crates");
        if ( objectLayer != null ) {
            for ( MapObject object : objectLayer.getObjects() ) {
                if ( object instanceof RectangleMapObject ) {

                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    float x = rect.x;
                    float y = rect.y;

                    y = map.getProperties().get("height", Integer.class) * map.getProperties()
                            .get("tileheight", Integer.class ) - y - rect.height;

                    String texturePath = (String) object.getProperties().get("texture");
                    if ( texturePath != null ) {
                        GameObject gameObject = new GameObject( texturePath , x , y );
                        gameObjects.add(gameObject);
                    }
                }
                // Handle other object types (PolygonMapObject, etc.) if needed
            }
        }
    }




    public void render(OrthographicCamera camera, SpriteBatch batch) {

        mapRenderer.setView(camera);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        mapRenderer.render(decorationLayerIndices);

        for (GameObject gameObject : gameObjects) {
            gameObject.render(batch);
        }

    }
    public void dispose() {

        if (map != null)    map.dispose() ;
        if (mapRenderer != null) mapRenderer.dispose() ;

    }
}
