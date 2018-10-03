package objects;

import framework.ObjectId;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;

public class Shockwave extends EnemyProjectile{

	private Animation gustAnim;
	private float origY;
	boolean flipX;

	public Shockwave(float x, float y, ObjectId id, Handler handler, double theta) {
		super(x, y, id, handler, theta);
		origY = y;
		sprite.setY(y - height);
		velY = 0;
		if(theta == 0) {
			velX = velocity * 2;
			flipX = true;
		} else {
			velX = velocity * -2;
		}
		sprite.setRotation(0);
		gustAnim = Assets.findAnimation("BossOneGust");
		gustAnim.speed = 0.15f;
		gustAnim.setCount(0);
	}

	public void tick(LinkedList<GameObject> object) {
		sprite.setPosition(sprite.getX() + velX * handler.deltaTime, sprite.getY() + velY * handler.deltaTime);
		checkOffScreen(object);
	}

	public void draw(SpriteBatch batch) {
		if(gustAnim.getCount() != gustAnim.size() - 1) {
			gustAnim.runAnimation();
		}
		sprite.setRegion(gustAnim.currentImg);
		height = (sprite.getRegionHeight() * gustAnim.getCount()) / 3.777f;
		sprite.setY(origY - height);
		sprite.setFlip(flipX, false);
		drawSprite(batch);
	}
}