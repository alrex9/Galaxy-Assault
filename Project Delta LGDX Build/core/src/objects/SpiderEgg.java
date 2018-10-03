package objects;

import java.util.LinkedList;

import framework.ObjectId;
import framework.Handler;
import framework.NewGame;

public class SpiderEgg extends GravityProjectile{

	public SpiderEgg(float x, float y, ObjectId id, Handler handler, double theta) {
		super(x, y, id, handler, theta);
		velocity /= NewGame.BASE_SPEED_MULTIPLYER;
		handler.addList.add(new Spiderling(sprite.getX(), sprite.getY() + sprite.getHeight(), handler, ObjectId.Enemy));
		handler.disposeList.add(this);
	}

	public void tick(LinkedList<GameObject> object) {
		move();
		if(velY < MAX_PROJECTILE_SPEED) {
			velY += gravity;
		} else {
			velY = MAX_PROJECTILE_SPEED;
		}
		if(checkOffScreen(object)) {
			handler.disposeList.add(this);
//			handler.addObject(new Spiderling(sprite.getX(), sprite.getY() + sprite.getHeight(), handler, ObjectId.Enemy));
		}
	}  
}
