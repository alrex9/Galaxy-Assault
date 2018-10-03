package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import framework.Handler;
import framework.ObjectId;

public class GravityProjectile extends EnemyProjectile {
	protected final static float MAX_PROJECTILE_SPEED = 10; //  needs to be a constant 

	public GravityProjectile(float x, float y, ObjectId id, Handler handler, double theta) {
		super(x, y, id, handler, theta);
	}
	
	public GravityProjectile(TextureRegion tex, float x, float y, int width, int height, ObjectId id, Handler handler, double theta) {
		super(x, y, id, handler, theta);
	}
	
	public GravityProjectile(TextureRegion tex, float x, float y, ObjectId id, Handler handler, double theta) {
		super(tex, x, y, id, handler, theta);
	}

	public void tick(LinkedList<GameObject> object) {
		sprite.setPosition(sprite.getX() + velX, sprite.getY() + velY);
		if(velY < MAX_PROJECTILE_SPEED) {
			velY += gravity;
		} else {
			velY = MAX_PROJECTILE_SPEED;
		}
		checkOffScreen(object);
	}  
}
