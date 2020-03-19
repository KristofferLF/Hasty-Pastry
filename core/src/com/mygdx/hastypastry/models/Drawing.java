package com.mygdx.hastypastry.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Stack;

/** The Drawing is an object containing all the lines as drawn in the GameView
 * It provides functionality to add lines and points, which are used by DrawingInputProcessor
 * addBodies(world) us used bu GameView for adding the bodies to the world when the user pushes play-btn.
 * GameView ask for all lines for drawing
 * @author sigmundhh */
public class Drawing {

    private Stack<ArrayList<Vector2>> lines = new Stack<>();
    private ArrayList<Body> bodies;

    public void addLine(ArrayList<Vector2> line) {
        lines.push(line);
    }

    public void addPoint(Vector2 point) {
        lines.peek().add(point);
    }

    public void undoLine(){
        lines.pop();
    }


    public void addBodies(World world) {
        bodies = new ArrayList<>();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = 2;
        fixtureDef.filter.maskBits = 1;

        for (ArrayList<Vector2> line : lines) {
            Body body = world.createBody(bodyDef);
            Vector2[] vertices = new Vector2[line.size()];
            line.toArray(vertices);
            ChainShape shape = new ChainShape();
            shape.createChain(vertices);
            fixtureDef.shape = shape;

            body.createFixture(fixtureDef);
            body.setUserData(false); //Not dangerous to collide with
            bodies.add(body);
            shape.dispose();
        }
    }

    public Stack<ArrayList<Vector2>> getLines() {
        return lines;
    }




}
