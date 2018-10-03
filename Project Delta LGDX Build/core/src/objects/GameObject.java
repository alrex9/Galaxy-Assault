package objects;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import framework.Handler;
import framework.NewGame;
import framework.ObjectId;

public abstract class GameObject {
	
	public float x, y, width, height, rotation;
	protected float gravity, waitTime, defaultWaitTime, scaleValue; // 0 = not jumping, 1 = jumped once, 2 = jump twice
	protected float damageTimer, theta, velX, velY, jumping;
	public float hp;
	protected ObjectId id;
	protected boolean wait, falling, takingDamage, flipX, flipY;
	protected Animation animaiton;
	public Handler handler;
	protected Rectangle boundingRectangle;

	public GameObject(float x, float y, Handler handler, ObjectId id) {
		this.handler = handler;
		this.id = id;
		gravity = NewGame.BASE_SPEED * 2;
		waitTime = 1;
		defaultWaitTime = 1;
		scaleValue = 1;
		falling = true;
		wait = false;
		boundingRectangle = new Rectangle(x, y, width, height);
	}

	public abstract void tick(LinkedList<GameObject> object);
	public Rectangle getBoundsBottom() {return null;}
	public Rectangle getBoundsTop() {return null;}
	public  Rectangle getBoundsLeft() {return null;}
	public  Rectangle getBoundsRight() {return null;}

	public float getVelX(){return this.velX;}
	public float getVelY(){return this.velY;}
	public void setVelX(float velX){this.velX = velX;}
	public void setVelY(float velY){this.velY = velY;}
	public ObjectId getId(){return id;}
	public float getJumping(){return this.jumping;}
	public void setJumping(float b){this.jumping = b;}
	public boolean getFalling(){return this.falling;}
	public void setFalling(boolean b){this.falling = b;}

	public void draw(SpriteBatch batch) {
		if(!flipY) {
			flipY = false;
		}
		drawSprite(batch);
	}

	public void myWait() {
		if(wait && waitTime <= 0) {
			waitTime -= handler.deltaTime;
		} else {
			waitTime = defaultWaitTime;
			wait = false;
		}
	}

	public  void move() {
		x = (x + velX * handler.deltaTime);
		y = (y + velY * handler.deltaTime);
	}

	public void setScale(float scale) {
		scaleValue = scale;
		width *= scaleValue;
		height *= scaleValue;
		y = (y + height);
	}

	// Points toward the CM of another GameObejct
	public float pointTowards(GameObject tempObject) { 
		theta =  (float) (Math.toDegrees((Math.atan2((y + height / 2) - (tempObject.y), (x + width / 2) - (tempObject.x + tempObject.width / 2)))) + 180);
		return theta;
	}

	// Points toward given coordinates
	public float PointTowards(int xOther, int yOther) { 
		return (float) (Math.toDegrees((Math.atan2((y) - (yOther /* + height of other / 2*/), (x) - (xOther /* + width of other / 2*/)))) + 180);
	}
	
	public float PointTowards(float thisX, float thisY, float xOther, float yOther) { 
		theta = (float) (Math.toDegrees((Math.atan2((thisY) - (yOther), (thisX) - (xOther)))) + 180);
		return theta;	
	}

	public void takeDamage() {
		if(takingDamage && damageTimer < takeDamageTime) {
			damageTimer += Gdx.graphics.getDeltaTime();
		} else {
			takingDamage = false;
			damageTimer = 0;
		}
	}
}
