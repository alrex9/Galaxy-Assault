package objects;

import java.awt.Point;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import framework.*;
import framework.NewGame.STATE;

/* Flying Boss: uses an array of locations to designate where to fly to in the sky
 * 3 attacks
 * 	1) fly by firing attack
 * 	2) targeted firing/missile (slow enough tracking to fly by and dodge)
 *	3) cluster bomb barrage // need to add
 *	4) charge
 */

public class FinalBoss extends Boss {
	// point 0 = regular start
	// point 1 = vertical start 
	// point 2 = upwards and left part of the screen
	// point 3 = upwards and right part of the screen
	// point 4 = off screen to the left
	// point 5 = off screen above
	private static final int WAVE_OMEGA_CONSTANT = 5;
	private final float DELTA_THETA = 3;
	protected int count, waveShotCount;
	protected float xAmp, yAmp, deltaTheta;
	protected Point nextPoint, tempPoint;
	protected boolean playingAnimation, justFired;
	protected double tempAngle;
	protected Animation swoop, sprayFire, gunShot;

	public FinalBoss(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findAnimation("BossFourFly").images[0], x, y - NewGame.V_HEIGHT / 12, handler, id);
		//		sprite.setSize(NewGame.V_WIDTH / 13, NewGame.V_HEIGHT / 7);
		maxStateNum = 6;
		minStateNum = 2;
		restTime = 1;
		hasGravity = false; 
		scaleValue = 0.5f;
		maxHp = 3;
		hp = maxHp;
		waveShotCount = r.nextInt(3) + 2;
		animations = new Animation[5];
		animations[0] = bossRun = Assets.findAnimation("BossFourFly");
		bossRun.speed = 0.075f;
		animations[1] = deathAnim = Assets.findAnimation("BossFourDeath");
		deathAnim.speed = 0.15f;
		animations[2] = sprayFire = Assets.findAnimation("BossFourSprayFire");
		sprayFire.speed = 0.075f;
		animations[3] = gunShot = Assets.findAnimation("BossFourGunShots");
		gunShot.speed = 0.075f;
		animations[4] = swoop = Assets.findAnimation("BossFourSwoop");
		swoop.speed = 0.025f;
		animationState = 0;
		sounds.add(Assets.findSound("FinalBossDeath.wav"));
		sounds.add(Assets.findSound("FinalBossFlying.wav"));
		sounds.add(Assets.findSound("FinalBossGetsHit.wav"));
		sounds.add(Assets.findSound("FinalBossShot.wav"));
		sounds.add(Assets.findSound("FinalBossWoosh.wav"));
		sounds.add(Assets.findSound("ProjectileRelease.wav"));
		//		sounds.add(Assets.findSound(""));
		reset();
	}

	public void specificStart() { 
		// points.add(new Point((int) sprite.getOriginX(), /*(int) sprite.getOrigin() - NewGame.V_WIDTH / 4*/ (int) sprite.getOriginY() + NewGame.V_HEIGHT / 4));
		points.add(new Point((int) sprite.getOriginX() - NewGame.V_WIDTH / 4, (int) /* (int) sprite.getOrigin() - NewGame.V_HEIGHT / 2*/ sprite.getOriginY())); 
		points.add(new Point((int) sprite.getOriginX() + NewGame.V_WIDTH / 4, (int) /*(int) sprite.getOrigin() - NewGame.V_HEIGHT*/ sprite.getOriginY())); 
		points.add(new Point((int) sprite.getOriginX() - NewGame.V_WIDTH / 4, (int) (sprite.getOriginY() - NewGame.V_HEIGHT / 6)));
		points.add(new Point((int) sprite.getOriginX() + NewGame.V_WIDTH / 4, (int) (sprite.getOriginY() - NewGame.V_HEIGHT / 2 + height)));
		//		System.out.println(sprite.getOriginX() + " " + sprite.getOriginY());
		//		for(int i = 0; i < points.size(); i++) {
		//			System.out.println(points.get(i));
		//		}
	}

	// remove this method later. This is only used for debugging	
	public void tick(LinkedList<GameObject> object) { // eventually to be removed
		System.out.println("movement state: " + movementState + ", Xpos: " + sprite.getX() + ", velX: " + velX + ", Ypos: " + sprite.getY() + ", "
				+ "velY: " + velY + ", rest time: " + restTime + ", rest count: " + restCount + ", next movement state " 
				+ nextMovementState + ", target point: " + targetPoint + moveCount + ", waiting " + wait/*+ ", next point: " + nextPoint*/);
		centerOfMass = new Vector2(sprite.getX() + (idleFrame.getRegionWidth() * scaleValue) / 2, sprite.getY() + (idleFrame.getRegionHeight() * scaleValue) / 2);
		if(autoKill) {
			hp = 0;
		}
		if (NewGame.State == STATE.BOSSSWORD) {
			velX = 0;
		}
		movements(object);
		attack(object);
	}

	// sets the state to idle and not running a script
	public void reset() { 
		movementState = 0;
		animationState = 0;
		runningScript = false;
		fired = false;
		secondaryFire = false;
	}

	public void action1() { // go to a location
		if(!secondaryFire) {
			xSpeed = NewGame.BASE_SPEED;
			ySpeed = NewGame.BASE_SPEED;
			calculateFlyVelocities();
		}
		if(moveTo(targetPoint, Math.abs(xSpeed), Math.abs(ySpeed))) {
			reset();
		} 
	}

	public void action2() { // go to a random, predesignated location
		targetPoint = points.get(r.nextInt(4));// new Point((int) startsprite.getX(), /*(int) sprite.getOrigin() - NewGame.V_WIDTH / 4*/ 400);
		//		xSpeed = NewGame.BASE_SPEED;
		//		ySpeed = NewGame.BASE_SPEED;
		//		calculateFlyVelocities();
		//		movementState = 1;
		reset();
	}

	public void action3() { // swoop attack
		if(!fired && sprite.getX() != sprite.getOriginX() && sprite.getX() != sprite.getOriginY()) {
			movementState = 1;
		} else if(!fired) {
			sounds.get(4).play(NewGame.gameVolume);
			targetPoint = new Point((int) (PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2), (int) (PlayScreen.player.sprite.getY() - PlayScreen.player.height / 2));
			xSpeed = NewGame.BASE_SPEED;
			ySpeed = NewGame.BASE_SPEED;
			xAmp = (float) ((sprite.getX() - PlayScreen.player.sprite.getX()));
			yAmp = (PlayScreen.player.sprite.getY() - sprite.getY());
			float fx = (float) (xAmp *  Math.cos(Math.PI) * Math.cos(0)); 
			float fy = (float) (-yAmp *  Math.sin(Math.PI) - sprite.getY());
			targetPoint = new Point((int) fx, (int) fy);
			tempPoint = new Point((int) sprite.getX(), (int) sprite.getY());
			fired = true;
		} else if(fired && Math.toDegrees(deltaTheta) < Math.toDegrees(DELTA_THETA)) {
			if(Math.abs(PlayScreen.player.sprite.getY() - sprite.getY()) < NewGame.V_HEIGHT / 12) {
				swoop.runAnimation();
				animationState = 4;
				sprite.setRegion(swoop.currentImg);
				playingAnimation = true;
			} else {
				playingAnimation = false;
			}
			deltaTheta += 3 * handler.deltaTime;
			//			System.out.println("xAmp: " + xAmp + " yAmp: " + yAmp + " delta theta: " + deltaTheta);
			sprite.setPosition((float) (xAmp *  Math.cos(deltaTheta) + tempPoint.getX() - xAmp * Math.cos(0)), (float) (yAmp *  Math.sin(deltaTheta) + tempPoint.getY())); // - xAmp * Math.cos(0)),
		} else { 
			playingAnimation = false;
			movementState = 1;
			nextMovementState = 2;
			restTime = 0;
			fired = false;
			targetPoint = new Point((int) (handler.gameCam.position.x - NewGame.V_WIDTH / 2 + NewGame.V_WIDTH / 12 + r.nextInt((int) (NewGame.V_WIDTH / 1.25))), (int) sprite.getOriginY());		
			sprite.setOrigin((float) targetPoint.getX(), (float) targetPoint.getY());
			deltaTheta = 0;
			fired = false;
		}
	}

	public void action4() { // flyby firing attack, redo logic
		playingAnimation = true;
		if(!fired) {
			if(PlayScreen.player.sprite.getX() <= sprite.getX()) {
				if(sprite.getX() != points.get(2).getX()){ 
					targetPoint = points.get(2);
				} else {
					targetPoint = points.get(3);
				}
			} else {
				if(sprite.getX() != points.get(3).getX()){ 
					targetPoint = points.get(3);
				} else {
					targetPoint = points.get(2);
				}
			}
			fired = true;
		}
		calculateFlyVelocities();
		if(moveTo(targetPoint, NewGame.BASE_SPEED, NewGame.BASE_SPEED)) { // if at target and the target isnt the starting point, go to starting point next
			targetPoint = points.get(0);
			playingAnimation = false;
			reset();			
		} else {
			sprayFire.runAnimation();
			animationState = 2;
			if(sprayFire.getCount() == 9 && secondaryFire == false) {
				sprite.setRegion(sprayFire.images[2]);
				sprayFire.setCount(2);
			} else {
				sprite.setRegion(sprayFire.currentImg);
			}
		}
		if(sprayFire.getCount() == 4 || sprayFire.getCount() == 6 || sprayFire.getCount() == 8) {
			justFired = false;
		}
		if((sprayFire.getCount() == 3 || sprayFire.getCount() == 5 || sprayFire.getCount() == 7) && !justFired) {
			sounds.get(3).play(NewGame.gameVolume);
			handler.addList.add(new EnemyProjectile(Assets.findRegion("FinalBossProjectile"), sprite.getX() + width / 2, sprite.getY() + height / 2 - height, NewGame.BASE_SPEED * 1.5f, 0.5f, ObjectId.EnemyProjectile, handler));
			justFired = true;
		}
		count++;
	}

	public void action5() { // fire homing missile, mostly animation and missle class
		//		pointTowards(PlayScreen.player);
		//		handler.addList.add(new HomingMissle(textures.turret_projectile[3], sprite.getX(), sprite.getY(), NewGame.V_WIDTH / 15, NewGame.V_HEIGHT / 14, ObjectId.EnemyProjectile, handler, theta));
		//		restTime = 4;
		//		nextMovementState =  1;
		reset();
	}

	// fly to the top middle of the screen and shoot wave shot
	public void action6() { 
		targetPoint = new Point((int) handler.gameCam.position.x, (int) (handler.gameCam.position.y - NewGame.V_HEIGHT / 7));
		calculateFlyVelocities();
		if(restCount > 0) {
			restCount -= handler.deltaTime;
		} else if(moveTo(targetPoint, NewGame.BASE_SPEED, NewGame.BASE_SPEED)) {
			playingAnimation = true;
			gunShot.runAnimation();
			animationState = 3;
			if(gunShot.getCount() == 9 && secondaryFire == false) {
				sprite.setRegion(gunShot.images[2]);
				restCount = (float) 0.5;
				gunShot.setCount(2);
				fired = false;
			} else if(gunShot.getCount() == gunShot.size() - 1) {
				playingAnimation = false;
				reset();
			} else {
				sprite.setRegion(gunShot.currentImg);
			}
			theta = PointTowards(sprite.getX() + width / 2, sprite.getY() - height * 3 / 2, PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2, PlayScreen.player.sprite.getY() - PlayScreen.player.height * 3 / 2);
			if(!fired && gunShot.getCount() == 3) {
				float drawX = sprite.getX();
				pointTowards(PlayScreen.player);
				if(sprite.getX() + width /2 < PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2) {
					drawX += width / 4;
				}
				for(int i = 0; i < 5; i++) {
					sounds.get(5).play(NewGame.gameVolume);
					handler.addList.add(new EnemyProjectile(Assets.findRegion("FinalBossProjectile"), drawX, sprite.getY() - (height * 1.3f), NewGame.BASE_SPEED * 1.777f, 0.5f, (float) (theta - (2 *  WAVE_OMEGA_CONSTANT) + (i * WAVE_OMEGA_CONSTANT)), ObjectId.EnemyProjectile, handler));
				}
				fired = true;
				waveShotCount --;
				//				System.out.println(waveShotCount);
			}
			if(waveShotCount < 1) {
				waveShotCount = r.nextInt(3) + 2;
				secondaryFire = true;
			}
		}
	}

	public void draw(SpriteBatch batch) {
		if (hp <= 0) {
			if(alive) {
				sounds.get(0).play(NewGame.gameVolume);
			}
			alive = false;
		}
		if(!playWalkSound && animationState == 0) {
			sounds.get(2).play(NewGame.gameVolume);
			playWalkSound = true;
		} else if(animationState != 0) { 
			playWalkSound = false;
		}
		if(!playDamagedSound && takingDamage) { 
			playDamagedSound = true;
			sounds.get(1).play(NewGame.gameVolume);
		}
		if(!playingAnimation && hp > 0) {
			bossRun.runAnimation(); 
			animationState = 0;
			sprite.setRegion(bossRun.currentImg);	
		}
		if(pointTowards(PlayScreen.player) - 90 < 0) {
			sprite.setFlip(true, true);
		} else {
			sprite.setFlip(false, true);
		}
		drawSprite(batch);
	}

	public void actionDeath() { 
		velX = 0;
		velY = 0;
		super.actionDeath();
	}
}