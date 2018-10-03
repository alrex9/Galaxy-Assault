package objects; 

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import brashmonkey.spriter.GDXPlayer;
import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.NewGame.STATE;
import framework.ObjectId;
import framework.PlayScreen;

public class Enemy extends GameObject {
	
	public GDXPlayer sprite;
	public float originX, originY, width, height, takeDamageTime;
	protected float gravity, waitTime, defaultWaitTime, scaleValue; // 0 = not jumping, 1 = jumped once, 2 = jump twice
	protected float damageTimer, velX, velY, jumping;
	public float hp;
	protected final float WALK_CONSTANT = NewGame.BASE_SPEED;
	public final float MAX_SPEED = (NewGame.BASE_SPEED * 1.5f);
	protected float deathTimer, restCount, restTime, restTimeDefault, scriptStartTime, currentVelX, currentVelY, xSpeed, ySpeed, travelTime, moveCount, accelY, damagedTimer;
	protected int movementState, nextMovementState, minStateNum, maxStateNum, animationState;
	protected boolean fired, variablesSet, alive, hasGravity, runningScript, atTarget, movedRight, movedLeft, secondaryFire, dealingDamage, innerRectangleOnly, thirdFired, playDamagedSound, playWalkSound;
	protected Animation deathAnim;
	protected ArrayList<Point> points;
	public ArrayList<Sound> sounds;
	protected BufferedImage currentArmImg;
	protected Point targetPoint;
	protected Random r;
	protected STATE activeState;
	public Rectangle topBounds, bottomBounds, leftBounds, rightBounds, constantDamageBox;
	public Player player;

	public Enemy(float x, float y, Handler handler, ObjectId id) { 
		super(x, y, handler, id);
		init();
	}

	public void init() { 
		takeDamageTime = 0.5777f;
		r = new Random();
		activeState = NewGame.STATE.GAME;
		points = new ArrayList<Point>();
		sounds = new ArrayList<Sound>();
		targetPoint = new Point(0,0);
		hp = 1; 
		maxStateNum = 2;
		minStateNum = 2;
		restTime = 1;
		restTimeDefault = 1;
		alive = true; 
		hasGravity = true;
		atTarget = true;
		fired = false;
		secondaryFire = false;
		variablesSet = false;
		runningScript = false;
		wait = false;
		calculateBounds();
	}

	public void tick(LinkedList<GameObject> object) {
		movements(object);
		attack(object);
	}

	public void movements(LinkedList<GameObject> object) { 
		//								System.out.println("movement state: " + movementState + ", Xpos: " + sprite.getX() + ", velX: " + velX + ", Ypos: " + sprite.getY() + ", velY: " + velY + ", rest time: " + restTime + ", rest count: " + restCount + ", next movement state " + nextMovementState + ", target point: " + targetPoint.toString() + ", fired: " + fired);
		if(movementState == 0) { // standing/rest
			action0();
		} else if(movementState == 1) { 
			action1();	
		} else if(movementState == 2) {
			action2();
		} else if(movementState == 3) {  
			action3();
		} else if(movementState == 4) {
			action4();
		} else if(movementState == 5) { 
			action5();
		} else if(movementState == 6) { 
			action6();
		} else if(movementState == 7) {
			action7();
		} else if(movementState == -1) { 
			actionDeath();
		} 
		physics(object);
	}

	public void action0() { 
		velX = 0;
		reset();
	}
	// go to a location
	public void action1() {
		if(moveTo(targetPoint, Math.abs(xSpeed), Math.abs(ySpeed))) {
			reset();
		} 
	}

	// default shooting action
	public void action2() {
		pointTowards(PlayScreen.player);
		handler.addList.add(new EnemyProjectile(sprite.getX(), sprite.getY(), ObjectId.EnemyProjectile, handler, theta));
		reset();
	}

	public void action3() { System.out.println("Missing action 3"); reset();}
	public void action4() { System.out.println("Missing action 4"); reset();}
	public void action5() { System.out.println("Missing action 5"); reset();}
	public void action6() { System.out.println("Missing action 6"); reset();}
	public void action7() { System.out.println("Missing action 7"); reset();}

	public void actionDeath() { 
		takingDamage = false;
		if(deathAnim.getCount() != deathAnim.size() - 1) {
			deathAnim.runAnimation();
			animationState = 1;
			setDeathAnim();
			restCount = 2;
		} else {
			restCount -= handler.deltaTime;
			if(restCount < 0) {
				if (this.getClass() == BossOne.class || this.getClass() == BossTwo.class || this.getClass() == BossThree.class || this.getClass() == FinalBoss.class) {
					NewGame.State = STATE.TRANSITIONFROMBOSS;
				}
//				System.out.println(this.id);
				handler.disposeList.add(this);
			}
		}
	}

	// sets the state to idle and not running a script
	public void reset() { 
		movementState = 0;
		animationState = -1;
		runningScript = false;
		fired = false;
		secondaryFire = false;
	}

	public void physics(LinkedList<GameObject> object) {
		move();
		if ((falling || jumping == 0) && hasGravity) {
			velY += gravity * handler.deltaTime;
			if (velY > MAX_SPEED) {
				velY = MAX_SPEED;
			}
		}
		Collision(object);
		takeDamage();
	}

	public void attack(LinkedList<GameObject> object) { 
		if(newScriptParameters()) { // enters the condition of setting movementState
			if(nextMovementState != 0) {
				movementState = nextMovementState;
				nextMovementState = 0;
			} else if(sprite.getX() < handler.gameCam.position.x - NewGame.V_WIDTH / 2 - width) {
				handler.disposeList.add(this);
			}else {
				movementState = r.nextInt(maxStateNum - minStateNum + 1) + minStateNum;
			}
			scriptStartTime = handler.runCount;
			restCount = 0;
			restTime = restTimeDefault;
			runningScript = true;
		} else if(!runningScript) { 
			movementState = 0;
			restCount += handler.deltaTime;
		}
		if(hp <= 0) {
			actionDeath();
			movementState = -1;
			velX = 0; 
		}
	}

	protected boolean newScriptParameters() {
		return NewGame.State == activeState && !runningScript && validScriptStartLocation() && restCount > restTime;
	}

	public void Collision(LinkedList<GameObject> object) {
		playerCollisions();
		
		calculateBounds();
		for (int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = object.get(i);
			if (tempObject.getId() == ObjectId.Block  && Math.abs(tempObject.sprite.getX() - sprite.getX()) < NewGame.V_WIDTH / 5 && Math.abs(tempObject.sprite.getY() - sprite.getY()) < NewGame.V_HEIGHT / 4) {
				blockCollisions(tempObject);
			}
		}
		movedRight = false;
		movedLeft = false;
	}

	protected void blockCollisions(GameObject tempObject) { 
		if (bottomBounds != null && hp > 0 && bottomBounds.overlaps(tempObject.boundingRectangle)) {
			sprite.setPosition(sprite.getX(), tempObject.sprite.getY() + 1);
			velY = 0;
			falling = false;
			jumping = 0;
		} else if (rightBounds == null && bottomBounds.overlaps(tempObject.boundingRectangle)) {
			velY = 0;
		} else {
			falling = true;
		}
		if (topBounds != null && topBounds.overlaps(tempObject.boundingRectangle)) { 
			System.out.println("setting bounds at top bounds");
			movedLeft = true;
			movedRight = true;
			sprite.setPosition(sprite.getX(), tempObject.sprite.getY() + tempObject.height + height + 1);
			velY = 0; 
		}
		if (leftBounds != null && !movedLeft && rightBounds.overlaps(tempObject.boundingRectangle)) {
			System.out.println("setting bounds at right bounds");
			movedLeft = true;
			sprite.setPosition(tempObject.sprite.getX() - width - 1, sprite.getY());
		} 
		if (rightBounds != null && !movedRight && leftBounds.overlaps(tempObject.boundingRectangle)) {
			System.out.println("setting bounds at left bounds");
			movedRight = true; 
			sprite.setPosition(tempObject.sprite.getX() + tempObject.width + 1,  sprite.getY());
		}
	}

	protected void playerCollisions() {
		if(!PlayScreen.player.takingDamage && !this.takingDamage && !PlayScreen.player.dashing && hp > 0 && PlayScreen.player.attackState == 0 && (NewGame.State == STATE.GAME || NewGame.State == STATE.BOSSFIGHT) &&
				((PlayScreen.player.boundingRectangle.overlaps(this.constantDamageBox) && !innerRectangleOnly)|| (PlayScreen.player.boundingRectangle.overlaps(this.boundingRectangle) && this.dealingDamage))) {
			PlayScreen.player.takingDamage = true;
			//			PlayScreen.player.hp--; // add back in to make player take damage
		} 
		if (PlayScreen.player.dashing && !this.takingDamage && PlayScreen.player.boundingRectangle.overlaps(this.boundingRectangle)) {
			this.takingDamage = true;
			this.hp--;
		}
	}

	protected void playerProjectileCollisions(GameObject tempObject) {
		//		if(sprite.getBoundingRectangle().overlaps(tempObject.boundingRectangle)) {
		//			velY = 0;
		//			velX = 0;
		//			handler.disposeList.add(tempObject);
		//			alive = false; // or takeDamageAnimation();
		//			hp -= /* an appropriate amount */ 1;
		//		}
	}

	public Rectangle getBoundsKillArea() {return new Rectangle(sprite.getX(), sprite.getY(),  sprite.getWidth(), ( sprite.getHeight() / 5));}

	protected void calculateBounds() {
		topBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height, width / 2, idleFrame.getRegionHeight() * scaleValue / 4);
		bottomBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height + (height * 3 / 4), width / 2, height / 4);
		leftBounds = new Rectangle( sprite.getX(), sprite.getY() - height + height  / 24, width / 4, height / 1.2f);
		rightBounds = new Rectangle(sprite.getX() + width * 3 / 4, sprite.getY() - height + height / 24, width / 4, height / 1.2f);
		constantDamageBox = new Rectangle(sprite.getX() + width / 4, sprite.getY() - height + height / 4, width / 2, height / 2);
	}

	public boolean moveTo(Point p, float velX, float velY){ // moves to a positions and is called for many ticks
		float fx = p.x;
		float fy = p.y;
		velX *= handler.deltaTime;
		velY *= handler.deltaTime;
		int correctCoordinates =  2;
		if(fx == 0 || sprite.getX() == fx){
			this.velX = 0;
			correctCoordinates--;
			if(fx != 0) { 
				x = fx;
			}
		} else if(fx <= sprite.getX()) { // moving left
			if(fx >= sprite.getX() - velX) {
				x = fx;
				this.velX = 0;
				correctCoordinates--;
			} else {
				this.velX = -velX / handler.deltaTime;
			}
		} else { // moving right
			if(fx <= sprite.getX() + velX) { // moving right
				x = fx;
				this.velX = 0;
				correctCoordinates--;
			} else {
				this.velX = velX / handler.deltaTime;
			}
		} 
		if(!hasGravity) {
			if(fy == 0 || sprite.getY() == fy) { // || ((fy - this.velY / handler.deltaTime > sprite.getY() && fy + this.velY / handler.deltaTime < sprite.getY()) || (sprite.getY() + this.velY / handler.deltaTime <= fy && sprite.getY() - this.velY / handler.deltaTime >= fy))) {
				this.velY = 0;
				correctCoordinates--;
				if(fy != 0) { 
					y = fy;;
				}
			} else if(sprite.getY() < fy) { // moving down
				if(fy <= sprite.getY() + velY) { 
					y = fy;;
					this.velY = 0;
					correctCoordinates--;
				} else {
					this.velY = velY / handler.deltaTime;
				}
			} else { // moving up
				if(fy >= sprite.getY() - velY) {
					y = fy;
					this.velY = 0;
					correctCoordinates--;
				} else {
					this.velY = -velY / handler.deltaTime;
				}
			}
		} else {
			correctCoordinates--;
		}
		if(correctCoordinates == 0) {
			atTarget = true;
			if(fx != 0) { 
				x = fx;
			}
			if(fy != 0) { 
				y = fy;
			}
			this.velX = 0;
			this.velY = 0;
			specialMoveToCases();
			moveCount = 0;
			return true;
		} else {
			atTarget = false;
		}
		return false;
	}

	protected void calculateFlyVelocities() {
		float deltaX = targetPoint.x - sprite.getX();
		float deltaY = sprite.getY() - targetPoint.y;
		if(Math.abs(deltaX) > Math.abs(deltaY)) {
			float travelTime = deltaX / xSpeed;
			ySpeed = deltaY / travelTime;
		} else {
			float travelTime = deltaY / ySpeed;
			xSpeed = deltaX / travelTime;
		}
		if(Float.isNaN(xSpeed)) {
			xSpeed = 0;
		} else if(Float.isNaN(ySpeed)) {
			ySpeed = 0;
		}
	}

	public void drawSprite(SpriteBatch batch) { 
		if(takingDamage) {
			batch.setColor(Color.RED);
		}
		batch.draw(sprite, sprite.getX(), sprite.getY() - height, width, height);
		batch.setColor(Color.WHITE);
		//		batch.draw(Assets.findRegion("BossBlock"), topBounds.getX(), topBounds.getY(), topBounds.getWidth(), topBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), bottomBounds.getX(), bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), leftBounds.getX(), leftBounds.getY(), leftBounds.getWidth(), leftBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), rightBounds.getX(), rightBounds.getY(), rightBounds.getWidth(), rightBounds.getHeight());
//		batch.draw(Assets.findRegion("BossBlock"), constantDamageBox.getX(), constantDamageBox.getY(), constantDamageBox.getWidth(), constantDamageBox.getHeight());
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY() - height, width,  height);
		//					batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}

	public boolean atTarget() {
		return (sprite.getX() != points.get(r.nextInt(points.size())).x || sprite.getY() !=  points.get(r.nextInt(points.size())).y);
	}

	public boolean validScriptStartLocation() {
		return !(sprite.getX() < handler.gameCam.position.x - NewGame.V_WIDTH / 2 ||  sprite.getX()  > (handler.gameCam.position.x) + NewGame.V_WIDTH / 2);
	}

	protected void specialMoveToCases() {
	}
	
	public void takeDamage() {
		if(takingDamage && damageTimer < takeDamageTime) {
			damageTimer += Gdx.graphics.getDeltaTime();
		} else {
			playDamagedSound = false;
			takingDamage = false;
			damageTimer = 0;
		}
	}
	
	public void dispose() {
		for(Sound s: sounds) { 
			s.stop();
		}
	}
	
	public void setDeathAnim() {
		
	}
}
