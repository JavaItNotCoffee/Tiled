package com.Dan.wasteland;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameObject {

    private Texture texture;
    private Vector2 position;
    private Rectangle bounds;

    public GameObject ( String texturePath , float x , float y ) {

        System.out.println( "Attempting to load texture: " + texturePath );
        try {
            this.texture = new Texture( texturePath );
            System.out.println( "Texture loaded successfully: " + texturePath );
        } catch ( Exception e ) {
            System.err.println( "Error loading texture: " + texturePath );
            e.printStackTrace();
        }

        this.position = new Vector2( x , y );

        float width = texture.getWidth();
        float height = texture.getHeight();

        this.bounds = new Rectangle( x , y , width , height );

    }

    public void render (SpriteBatch batch) {

        System.out.println("Rendering object at: " + position.x + ", " + position.y);
        batch.draw( texture , position.x , position.y );
    }

    public boolean collidesWith ( Rectangle other ) {
        return bounds.overlaps(other);
    }

    public Rectangle getBounds () {
        return bounds;
    }

    public void dispose () {
        texture.dispose();
    }

}
