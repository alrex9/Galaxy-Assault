package objects;

import java.awt.Point;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;
import framework.PlayScreen;

/* Flying Boss: uses an array of locations to designate where to fly to in the sky
 * 3 attacks
 * 	1) fly by firing attack
 * 	2) targeted firing/missile (slow enough tracking to fly by and dodge)
 *	3) cluster bomb barrage // need to add
 *	4) charge
 */

public class BossThree extends Boss {

	private final float CHARGE_CONSTANT = (float) (NewGame.BASE_SPEED * 1.5);
	protected boolean playingAnimation, flipX;
	private Animation charge, magnet, hammerThrow;
	private float xAmp;

	public BossThree(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findRegion("BossThreeIdle"), x, y - NewGame.V_HEIGHT/ 4, handler, id);
		maxStateNum = 5;
		minStateNum = 2;
		restTime = 1;
		maxHp = 3;
		hp = maxHp;
		animations = new Animation[5];
		animations[0] = bossRun = Assets.findAnimation("BossThreeWalk");
		bossRun.speed = 0.075f;
		animations[1] = deathAnim = Assets.findAnimation("BossThreeDeath");
		deathAnim.speed = 0.27f;
		animations[2] = charge = Assets.findAnimation("BossThreeCharge");
		charge.speed = 0.07775f;
		animations[3] = magnet = Assets.findAnimation("BossThreeMagnet");
		magnet.speed = 0.07775f;
		animations[4] = hammerThrow = Assets.findAnimation("BossThreeThrowing");
		hammerThrow.speed = 0.07775f;
		scaleValue = 0.7775f;
		reset();
		sounds.add(Assets.findSound("HammerThrow.wav"));
		sounds.add(Assets.findSound("RhinoCharge.wav"));
		sounds.add(Assets.findSound("RhinoDeath.wav"));
		sounds.add(Assets.findSound("RhinoGetsHit.wav"));
		sounds.add(Assets.findSound("RobotWalking.wav"));
	}
	
	public void tick(LinkedList<GameObject> object) {
		super.tick(object);
	}

	public void action1() { // go to a location
		if(moveTo(targetPoint, Math.abs(xSpeed), Math.abs(ySpeed))) {
			sprite.setRegion(idleFrame);
			reset();
		} //kinda suprised the code below this works
		bossRun.runAnimation(); 
		animationState = 0;
		sprite.setRegion(bossRun.currentImg);
	}

	public void action2() { // walk off the screen in the opposite direction of the player
		playingAnimation = true;
		if(charge.getCount() != charge.size() - 1) {
			charge.runAnimation();
			sprite.getColor();
			animationState = 2;
			sprite.setRegion(charge.currentImg);
		}
		if(!fired && charge.getCount() == charge.size() - 1) {
			if(PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2 > sprite.getX() + width / 2) {
				velX = 2 * CHARGE_CONSTANT;
				flipX = true;
			} else {
				velX = -2 * CHARGE_CONSTANT;
				flipX = false;
			}
			sounds.get(1).play(NewGame.gameVolume);
			fired = true;
		} else if(fired) { 
			hasGravity = false;
			if(sprite.getX() <= handler.gameCam.position.x - NewGame.V_WIDTH / 2 - width) { // left of the screen
				sprite.setX(handler.gameCam.position.x + NewGame.V_WIDTH / 2 + width);
				setPostChargeConditions();
			} else if(sprite.getX() >= handler.gameCam.position.x + NewGame.V_WIDTH / 2) { // right of the screen
				sprite.setX(handler.gameCam.position.x - NewGame.V_WIDTH / 2 - width * 2);
				setPostChargeConditions();
			}
		}
	}

	private void setPostChargeConditions() {
		innerRectangleOnly = true;
		xSpeed = WALK_CONSTANT;
		playingAnimation = false;
		targetPoint = new Point((int) (handler.gameCam.position.x - width / 2), 0);
		fired = false;
		movementState = 1;
		bossRun.setCount(0);
		charge.setCount(0);
		//		sprite.setFlip(sprite.isFlipX(), false);
	}

	public void action3() {
		if(!secondaryFire) {
			magnet.runAnimation();
			animationState = 3;
			sprite.setRegion(magnet.currentImg);
		} else {
			restCount -= handler.deltaTime;
			if(restCount < 0) {
				restCount = 0;
				secondaryFire = false;
				magnet.setCount(7);
			}
		}
		if(magnet.getCount() == 6 && !fired) {
			secondaryFire = true;
			System.out.println("adding magnet");
			handler.addList.add(new MagHammer(Assets.findRegion("Hammer"), sprite.getX() + width / 1.2f, (float) (sprite.getY() - height + height / 4),  ObjectId.EnemyProjectile, handler, 0));
			handler.addList.add(new MagHammer(Assets.findRegion("Hammer2"), sprite.getX(), (float) (sprite.getY() - height + height / 4), ObjectId.EnemyProjectile, handler, 180));
			fired = true;
			restCount = (float) 1.8577;
		} if(fired && magnet.getCount() == 0) {
			reset();
			animationState = -1;
			magnet.setCount(0);
		}
	}

	public void action4() {
		hammerThrow.runAnimation();
		animationState = 4;
		sprite.setRegion(hammerThrow.currentImg);
		xAmp = (float) (PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2 - sprite.getX());
		float calcX = sprite.getX();
		if (xAmp < 0) {
			flipX = false;
			//			xAmp -= Assets.findRegion("Hammer").getRegionWidth() / 1.5f;
		} else {
			System.out.println("negatively adjusting hammer thrrow amp");
			flipX = true;
			calcX += width / 2;
			//			xAmp -= (Assets.findRegion("Hammer").getRegionWidth() * 2);
			xAmp -= width;
		}

		if(!fired && hammerThrow.getCount() == 4) {
			handler.addList.add(new ThrowHammer(Assets.findRegion("Hammer"), calcX, sprite.getY() - height, ObjectId.EnemyProjectile, handler, -90 + Math.toDegrees(Math.atan2(xAmp / 2, NewGame.V_HEIGHT / 2))));
			fired = true;
			sounds.get(0).play(NewGame.gameVolume);
		} else if(!secondaryFire && hammerThrow.getCount() == 5) {
			handler.addList.add(new ThrowHammer(Assets.findRegion("Hammer2"), calcX, sprite.getY() - height, ObjectId.EnemyProjectile, handler, -90 + Math.toDegrees(Math.atan2(xAmp / 2, NewGame.V_HEIGHT/2))));
			secondaryFire = true;
			fired = false;
//			sounds.get(0).play(NewGame.gameVolume);
		} else if(!fired && hammerThrow.getCount() == 6) {
			handler.addList.add(new ThrowHammer(Assets.findRegion("Hammer"), calcX, sprite.getY() - height, ObjectId.EnemyProjectile, handler, -90 + Math.toDegrees(Math.atan2(xAmp / 2, NewGame.V_HEIGHT/2))));
			secondaryFire = false;
			fired = true;
//			sounds.get(0).play(NewGame.gameVolume);
		} else if(!secondaryFire && hammerThrow.getCount() == 7) {
			handler.addList.add(new ThrowHammer(Assets.findRegion("Hammer2"), calcX, sprite.getY() - height, ObjectId.EnemyProjectile, handler, -90 + Math.toDegrees(Math.atan2(xAmp / 2, NewGame.V_HEIGHT/2))));
			secondaryFire = true;
			sounds.get(0).play(NewGame.gameVolume);
		} else if(fired && hammerThrow.getCount() == 0) {
			reset();
			System.out.println("reseting hammer throw");
			hammerThrow.setCount(0);
		}
	}

	public void action5() {
		targetPoint = new Point((int) (handler.gameCam.position.x - NewGame.V_WIDTH / 2.1f + r.nextInt((int) (NewGame.V_WIDTH / 1.2f - idleFrame.getRegionWidth() * scaleValue))), 0); 
		movementState = 1;
		restCount = 2;
	}


	public void draw(SpriteBatch batch) {
		if (hp <= 0) {
			if(alive) {
				sounds.get(2).play(NewGame.gameVolume);
			}
			alive = false;
		}
		if(!playWalkSound && animationState == 0) {
			sounds.get(4).play(NewGame.gameVolume);
			playWalkSound = true;
		} else if(animationState != 0) { 
			playWalkSound = false;
		}
		if(!playDamagedSound && takingDamage) { 
			playDamagedSound = true;
			sounds.get(3).play(NewGame.gameVolume);
		}
		if(movementState == 0) {
			sprite.setRegion(idleFrame);
			animationState = -1;
			if(sprite.getX() + width / 2 < PlayScreen.player.sprite.getX() +  PlayScreen.player.width / 2) {
				flipX = true;
			} else {
				flipX = false;
			}
		}
		if(!sprite.isFlipY()) {
			sprite.flip(flipX, true);
		}
		drawSprite(batch);
	}

	protected void calculateBounds() {
		if (hp <= 0) {
			if(alive) {
				sounds.get(0).play(NewGame.gameVolume);
			}
			alive = false;
			rightBounds = null;
			leftBounds = null; 
		} else { 
			leftBounds = new Rectangle( sprite.getX(), sprite.getY() - height + height  / 24, width / 4, height / 1.5f);
			rightBounds = new Rectangle(sprite.getX() + width * 3 / 4, sprite.getY() - height + height / 24, width / 4, height / 1.5f);
		}
		bottomBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height + (height * 3 / 4), width / 2, height / 4);
		topBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height, width / 2, idleFrame.getRegionHeight() * scaleValue / 4);
		constantDamageBox = new Rectangle(sprite.getX() + width / 4, sprite.getY() - height + height / 4, width / 2, height / 2);
	}
}