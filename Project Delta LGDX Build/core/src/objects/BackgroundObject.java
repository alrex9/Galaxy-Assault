package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import framework.*;

public class BackgroundObject extends GameObject{
	
	public BackgroundObject(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findRegion("MiddlePink"), x, y, handler, id);
	}
	
	public void tick(LinkedList<GameObject> object) {
		// TODO Auto-generated method stub
		
	}

	public Rectangle getBoundsBottom() {return null;}
	public Rectangle getBoundsTop() {return null;}
	public Rectangle getBoundsLeft() {return null;}
	public Rectangle getBoundsRight() {return null;}

	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub
		
	}
}
