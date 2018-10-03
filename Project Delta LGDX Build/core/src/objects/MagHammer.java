package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import framework.Handler;
import framework.NewGame;
import framework.ObjectId;

public class MagHammer extends EnemyProjectile {
	protected float accelX, startX;
	private boolean flipX;

	public MagHammer(TextureRegion tex, float x, float y, ObjectId id, Handler handler, double theta) {
		super(tex, x, y, id, handler, theta);
		startX = x;
		if(theta == 0) {
			sprite.setOriginCenter();
			sprite.setRotation(90);
			flipX = false;
			velX = NewGame.BASE_SPEED * 2;
			accelX = -NewGame.BASE_SPEED; 
			theta = -90;
		} else {
			sprite.setOriginCenter();
			sprite.setRotation(-90);
			flipX = true;
			velX = -NewGame.BASE_SPEED * 2;
			accelX = NewGame.BASE_SPEED; 
			theta = 90;
		}
	}

	public void tick(LinkedList<GameObject> object) {
		sprite.setPosition(sprite.getX() + velX * handler.deltaTime, sprite.getY());
		if(flipX && sprite.getX() > startX) {
			handler.disposeList.add(this);
		} else if(!flipX && sprite.getX() < startX) {
			handler.disposeList.add(this);
		} else {
			if(theta == 0.0) {
				velX += accelX * handler.deltaTime;
				accelX += accelX * handler.deltaTime;
			} else {
				velX += accelX * handler.deltaTime;
				accelX += accelX * handler.deltaTime;
			}
		}
	}
	
	public void draw(SpriteBatch batch) {
		if(!sprite.isFlipY()) {
			sprite.flip(flipX, true);
		}
		drawSprite(batch);
	}
	
	public void drawSprite(SpriteBatch batch) { 
		if(flipX) {
			theta = 90;
		} else {
			theta = -90;
		}
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(), width, height, 1f, 1f, (float) (theta - 180));
		boundingRectangle = new Rectangle(sprite.getX() + width / 8, sprite.getY() + height / 8, width - (width / 4), height + (height / 4)); // Swapped out old calculations that multiplied original image size by the width and height
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
