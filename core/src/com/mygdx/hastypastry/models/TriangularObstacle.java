package com.mygdx.hastypastry.models;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hastypastry.Assets;

public class TriangularObstacle extends Obstacle{

    public TriangularObstacle(Assets assets, World world, float posX, float posY, float width, float height, Boolean isDeadly){
        super(world, posX, posY, width, height, isDeadly);

        if (isDeadly){
            sprite.setRegion(assets.getManager().get(Assets.gameTextures).findRegion("deadlytriangle"));
        } else {
            sprite.setRegion(assets.getManager().get(Assets.gameTextures).findRegion("triangle"));
        }

        PolygonShape shape = new PolygonShape();
        float[] vertices = {-width/2, -height/2, width/2, -height/2, 0, height/2};
        shape.set(vertices);

        setFixture(shape);
    }
}
