package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;
import framework.PlayScreen;

public class Turret extends Enemy {

	protected boolean upwards;
	protected int difficulty;
	protected float deltaTheta, recoil;
	protected final int RECOIL_THETA = 55;
	protected Sprite base;

	public Turret(float x, float y, int difficulty, boolean upwards, Handler handler, ObjectId id) {
		super(Assets.findRegion("UpperTurret"),x, y, handler, id);
		activeState = NewGame.STATE.BOSSFIGHT;
		this.upwards = upwards;
		this.difficulty = difficulty;
		sprite.setSize(NewGame.V_WIDTH / 20, NewGame.V_HEIGHT / 16);
//		idleFrame = textures.turret_upper[difficulty];
		base = new Sprite(Assets.findRegion("LowerTurret"));
		base.setPosition(x, y); 
		base.setSize(NewGame.V_WIDTH / 18, NewGame.V_HEIGHT / 14);
		if(!upwards) {
			sprite.setPosition(x, y + base.getHeight() - sprite.getHeight() / 2);
			base.setFlip(sprite.isFlipX(), false);
		} else {
			sprite.setPosition(x, y - base.getHeight() + sprite.getHeight() / 2);
			base.setFlip(sprite.isFlipX(), true);
		}
		sprite.setOrigin(sprite.getX(), sprite.getY());
		maxStateNum = 2;
		minStateNum = 2;
		hasGravity = false;
		secondaryFire = true;
		//		for(GameObject object : handler.object) {
		//			if(object.getId() == ObjectId.Block) {
		//				if(object.sprite.getY() + object.sprite.getHeight() + 1 == y) {
		//					upwards = true;
		//					break;
		//				} else if(object.sprite.getY() + object.sprite.getHeight() + 1 == y) {
		//					upwards = false; 
		//					break;
		//				}
		//			}
		//		}
		sprite.setOriginCenter();
		sounds.add(Assets.findSound("ProjectileRelease"));
	}

	public void tick(LinkedList<GameObject> object) {
		pointTowards(PlayScreen.player);
		movements(object);
		attack(object);
		mechanics();
	}

	public void mechanics() {
		if(theta > 270 || theta < 90) {
			sprite.setFlip(true, true);
		} else {
			sprite.setFlip(true, false);
		}
		if(!secondaryFire) { 
			recoil = RECOIL_THETA * handler.deltaTime * 8;
			if(!fired) {
				recoil /= 4;
			}
			deltaTheta += recoil;
		}

		if(fired && deltaTheta < RECOIL_THETA) {
			if(!sprite.isFlipY()) { 
				sprite.rotate(recoil); 
			} else {
				sprite.rotate(-recoil); 
			}
		} else if(fired && deltaTheta > RECOIL_THETA) {
			fired = false;
			deltaTheta = 0;
		}
		if(!fired && !secondaryFire && deltaTheta < RECOIL_THETA) {
			if(!sprite.isFlipY()) { 
				sprite.rotate(-recoil); 
			} else {
				sprite.rotate(recoil); 
			}
		} else if(!fired && !secondaryFire && deltaTheta > RECOIL_THETA) {
			deltaTheta = 0;
			movementState = 0;
		}
		if(movementState != 1) {
			sprite.setRotation((float) theta);
		}
	}	
	public void physics(LinkedList<GameObject> object) {}

	public void reset() { // sets the state to idle and not running a script
		movementState = 0;
		runningScript = false;
		fired = false;
		secondaryFire = true;
	}

	public void action1() {}

	public void action2() { // default shooting action
//		textures.turret_projectile[difficulty]
		sounds.get(0).play(NewGame.gameVolume);
		if(!fired) {
			handler.addList.add(new EnemyProjectile(Assets.findRegion("EasyProjectile"), (float) (sprite.getX() + width / 2), (float) (sprite.getY() - height / 2), ObjectId.EnemyProjectile, handler, theta));
		}
		fired = true;
		secondaryFire = false;
		movementState = 1;
		restTime = 3;
	}

	public void draw(SpriteBatch batch) {
		base.draw(batch);
		drawSprite(batch);
		//		System.out.println(sprite.getX() + " , " + sprite.getY() + " / " + base.getX() + " , " + base.getY());
	}
}
