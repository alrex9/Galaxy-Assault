package objects;

import java.util.LinkedList;

import framework.ObjectId;
import framework.Handler;
import framework.NewGame;

public class BouncingProjectile extends GravityProjectile{

	protected final float DEFAULT_MAX_VELOCITY = (float) (NewGame.BASE_SPEED * 5);
	
	public BouncingProjectile(float x, float y, ObjectId id, Handler handler, double theta) {
		super(x, y, id, handler, theta);
	}
	
	public void tick(LinkedList<GameObject> object) {
		sprite.setPosition(sprite.getX() + velX * handler.deltaTime, sprite.getY() + velY * handler.deltaTime);
//		if(velY < DEFAULT_MAX_VELOCITY) {
			velY += gravity * 500;
//			System.out.println(velY);
//		} else {
//			velY = DEFAULT_MAX_VELOCITY;
//		}
		for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            if (tempObject.getId() == ObjectId.Block) {
                if (getBoundsFull().overlaps(tempObject.sprite.getBoundingRectangle())) {
                	sprite.setY(tempObject.sprite.getY() - sprite.getHeight()); 
                    velY = -velY;
                    if(velX > 0) {
                    	velX += NewGame.BASE_SPEED / 20;
                    } else {
                    	velX -= NewGame.BASE_SPEED / 20;
                    }
                }
            }
		}

		checkOffScreen(object);
	}
}
