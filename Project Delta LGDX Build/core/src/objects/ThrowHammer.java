package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import framework.Handler;
import framework.NewGame;
import framework.ObjectId;

public class ThrowHammer extends GravityProjectile {
	protected float omega = 77.7f;
	private float MAX_PROJECTILE_SPEED = NewGame.BASE_SPEED * 5;

	public ThrowHammer(TextureRegion tex, float x, float y, ObjectId id, Handler handler, double theta) {
		super(tex, x, y, id, handler, theta);
		this.velocity = NewGame.BASE_SPEED * 5;
		velX = (float) (velocity * Math.cos(Math.toRadians(theta)));
		velY = (float) (velocity * Math.sin(Math.toRadians(theta)));
	}
	
	public void tick(LinkedList<GameObject> object) {
		checkOffScreen(object);
		sprite.setPosition(sprite.getX() + velX * handler.deltaTime, sprite.getY() + velY * handler.deltaTime);
		if(velY < MAX_PROJECTILE_SPEED) {
			velY += NewGame.BASE_SPEED * 15 * handler.deltaTime;
		} else {
			velY = MAX_PROJECTILE_SPEED;
		}
		sprite.setOriginCenter();
		omega += handler.deltaTime * 720;
		theta += omega;
//		sprite.rotate(omega * handler.deltaTime);
	}
	
	public void drawSprite(SpriteBatch batch) { 
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(), width, height, 1f, 1f, (float) (theta - 180));
		boundingRectangle = new Rectangle(sprite.getX() + width / 8, sprite.getY() + height / 8, width - (width / 4), height + (height / 4)); // Swapped out old calculations that multiplied original image size by the width and height
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
