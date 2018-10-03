package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;

public class Projectile extends GameObject {
	protected double theta;
	protected float velocity;
	public Sprite sprite;

	public Projectile(float x, float y, ObjectId id, Handler handler, double theta) {
		this(Assets.findRegion("EasyProjectile"), x, y, id ,handler, 0);
		//		this.velocity = NewGame.BASE_SPEED  * NewGame.BASE_SPEED_MULTIPLYER;
		this.velocity = NewGame.BASE_SPEED;
		constuctorHelper(handler, theta);
	}

	public Projectile(TextureRegion tex, float x, float y, ObjectId id, Handler handler, double theta) {
		super(x, y, handler ,id);
		sprite = new Sprite(tex);
		sprite.setPosition(x, y);
		sprite.setOrigin(x, y);
		this.velocity = NewGame.BASE_SPEED * 3;
		constuctorHelper(handler, theta);
	}
	
	public Projectile(TextureRegion tex, float x, float y, ObjectId id, Handler handler) {
		super(x, y, handler ,id);
	}

	protected void constuctorHelper(Handler handler, double theta) {
		this.handler = handler;
		this.theta = theta;
		sprite.setOriginCenter();
		sprite.setRotation((float)Math.toDegrees(theta) - 180);
		velX = (float) (velocity * Math.cos(Math.toRadians(theta)));
		velY = (float) (velocity * Math.sin(Math.toRadians(theta)));
		setScale(scaleValue);
	}

	public void tick(LinkedList<GameObject> object) {
//		System.out.println(sprite.getX() + ", " + sprite.getY());
		move();
		checkOffScreen(object);
	}

	public Rectangle getBoundsFull() {
		return new Rectangle ((int)sprite.getX(),(int)sprite.getY(), width, height);
	}

	public boolean checkOffScreen(LinkedList<GameObject> object) {
		if((sprite.getX() + width < handler.gameCam.position.x - NewGame.V_WIDTH / 2 || sprite.getX() > handler.gameCam.position.x + NewGame.V_WIDTH / 2)
				||(sprite.getY() > handler.gameCam.position.y + NewGame.V_HEIGHT / 2 || sprite.getY() + height < handler.gameCam.position.y - NewGame.V_HEIGHT / 2)) {
			handler.disposeList.add(this);
			return true;
		}
		return false;
	}

	public void draw(SpriteBatch batch) {
		if(!sprite.isFlipY()) {
			sprite.flip(false, true);
		}
		drawSprite(batch);
	}

	public void drawSprite(SpriteBatch batch) { 
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(), width, height, 1f, 1f, (float) (theta - 180));
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY(), width, height); // Swapped out old calculations that multiplied original image size by the width and height
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}


