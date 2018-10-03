package objects;

import java.util.LinkedList;
import framework.Handler;
import framework.ObjectId;

public class WaveProjectile extends EnemyProjectile{
	private static  int DISTANCE_MULTIPLYER = 2;
	protected float DISPLACEMENT_CONSTANT;
	protected float xSpeed, ySpeed, travelDistance,x ,y;
	
	// g(x) = Asin(x) + x + Xo or something like that
	
	public WaveProjectile(float x, float y, ObjectId id, Handler handler, double theta) {
		super(x, y, id, handler, theta);
		this.x = x;
		this.y = y;
		travelDistance = (float) (xSpeed / Math.cos(theta));
	}
	
	public void tick(LinkedList<GameObject> object) {
		System.out.println(x + " , " + y +" vel: " + velX + " , " + velY);
		x = sprite.getX() + velX/ 20 * handler.deltaTime;
		y = sprite.getY() + velY / 20 * handler.deltaTime;
		sprite.setPosition((float) (Math.sin(x) + x + sprite.getOriginX()), y);
		checkOffScreen(object);
	}
	
	private void calculateFlyVelocities() { // string represents the x or y value travel time will be based off of
		float deltaX =  xSpeed  * DISTANCE_MULTIPLYER;
		float deltaY = ySpeed * DISTANCE_MULTIPLYER;
		if(Math.abs(deltaX) > Math.abs(deltaY)) {
			float travelTime = deltaX / xSpeed;
			ySpeed = deltaY / travelTime;
		} else {
			float travelTime = deltaY / ySpeed;
			xSpeed = deltaX / travelTime;
		}
		if(Float.isNaN(xSpeed)) {
			xSpeed = 0;
		} else if(Float.isNaN(ySpeed)) {
			ySpeed = 0;
		}
	}

}
