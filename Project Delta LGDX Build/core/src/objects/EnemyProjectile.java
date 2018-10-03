package objects;

import framework.ObjectId;
import framework.PlayScreen;

import java.util.LinkedList;

import javax.swing.SpringLayout.Constraints;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import framework.Assets;
import framework.Handler;
import framework.NewGame;

public class EnemyProjectile extends Projectile{

	public EnemyProjectile(float x, float y, ObjectId id, Handler handler, double theta) {
		super(x, y, id, handler, theta);
	}

	public EnemyProjectile(TextureRegion tex, float x, float y, ObjectId id, Handler handler, double theta) {
		super(tex, x, y, id, handler, theta);
	}
	
	
	public EnemyProjectile(TextureRegion tex, float x, float y, float velocity, float scaleValue, ObjectId id, Handler handler) {
		super(tex, x, y, id, handler);
		this.velocity = velocity;
		this.scaleValue = scaleValue;
		this.theta = pointTowards(PlayScreen.player);
		constuctorHelper(handler, this.theta);
	}
	
	public EnemyProjectile(TextureRegion tex, float x, float y, float velocity, float scaleValue, float theta, ObjectId id, Handler handler) {
		super(tex, x, y, id, handler);
		this.theta = theta;
		this.velocity = velocity;
		this.scaleValue = scaleValue;
		constuctorHelper(handler, theta);
	}
	
	protected void constuctorHelper(Handler handler, double theta) {
		this.handler = handler;
		sprite.setOriginCenter();
		sprite.setRotation((float)Math.toDegrees(theta)- 180);
		velX = (float) (velocity * Math.cos(Math.toRadians(this.theta)));
		velY = (float) (velocity * Math.sin(Math.toRadians(this.theta)));
		setScale(scaleValue);
	}
	
	public void tick(LinkedList<GameObject> object) {
		move();
		checkOffScreen(object);
	}
	
	public void setScale(float scale) {
		scaleValue = scale;
		width = sprite.getWidth() * scaleValue;
		height = sprite.getHeight() * scaleValue;
		sprite.setY(sprite.getY());
	}
	
	public void draw(SpriteBatch batch) {
		if(!sprite.isFlipY()) {
			sprite.flip(false, true);
		}
		drawSprite(batch);
	}

	public void drawSprite(SpriteBatch batch) { 
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(), width, height, 1f, 1f, (float) (theta - 180));
		boundingRectangle = new Rectangle(sprite.getX() + width / 8, sprite.getY() + height / 8, width - (width / 4), height + (height / 4)); // Swapped out old calculations that multiplied original image size by the width and height
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
