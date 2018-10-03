package objects;

import java.awt.Point;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import framework.ObjectId;
import framework.PlayScreen;
import framework.NewGame.STATE;
import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;

public class Spiderling extends Minion {

	private final float DELTA_THETA = 3;
	private boolean onGround, singleFire;
	private float xAmp, yAmp, deltaTheta, fallTheta;
	private Point tempPoint;
	protected Animation spiderlingFly;

	public Spiderling(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findAnimation("BugFlying").currentImg, x, y, handler, id);
		movementState = 0;
		maxStateNum = 2;
		minStateNum = 2;
		restTime = 1; // to give it time to initalize
		restTimeDefault = 1;
		hasGravity = false;
		onGround = false;
		velY = 0;
		scaleValue = 0.5f;
		animations = new Animation[2];
		animations[0] = spiderlingFly = new Animation(Assets.findAnimation("BugFlying")); // make this reference not static by making a new animation object and creating a new constructor in the animation class to make this easy
		spiderlingFly.setCount(0);
		animations[1] = deathAnim = new Animation(Assets.findAnimation("ExplosionAnim"));
		deathAnim.speed = 0.12777f;
		deathAnim.setCount(0);
		sprite.setOrigin(x, y);
		if(sprite.getOriginX() < handler.gameCam.position.x) {
			targetPoint = new Point((int) handler.gameCam.position.x - NewGame.V_WIDTH / 4, (int) handler.gameCam.position.y);
		} else {
			targetPoint = new Point((int) handler.gameCam.position.x + NewGame.V_WIDTH / 4, (int) handler.gameCam.position.y);
		}
		hp = 1;
		animationState = 0;
		movementState = 1;
		dealingDamage = false;
		innerRectangleOnly = false;
		sounds.add(Assets.findSound("BugBuzz.wav"));
		sounds.add(Assets.findSound("BugPoisonShot.wav"));
//		sounds.add(Assets.findSound("ProjectileExplosion.wav"));
		sounds.get(0).loop(NewGame.gameVolume);
	}

	public void tick(LinkedList<GameObject> object) {
		//		System.out.println("movement state: " + movementState + ", Xpos: " + sprite.getX() + ", velX: " + velX + ", Ypos: " + sprite.getY() + ", velY: " + velY + ", rest time: " + restTime + ", rest count: " + restCount + ", next movement state " + nextMovementState + ", target point: " + targetPoint.toString() + ", fired: " + fired);
		if(NewGame.State != STATE.BOSSFIGHT) {
			this.hp = 0;
		}
		movements(object);
		attack(object);
	}

	protected boolean newScriptParameters() {
		return NewGame.State == activeState && !runningScript && restCount > restTime;
	}

	public void action1() { // go to a location
		if(!secondaryFire) {
			xSpeed = NewGame.BASE_SPEED;
			ySpeed = NewGame.BASE_SPEED;
			calculateFlyVelocities();
		}
		if(moveTo(targetPoint, Math.abs(xSpeed), Math.abs(ySpeed))) {
			reset();
			if(!singleFire) {
				singleFire = true;
				sprite.setOrigin(sprite.getX(), sprite.getY());
			}
		} 
	}

	public void action2() { // swoop attack
		if(!fired && sprite.getX() != sprite.getOriginX() && sprite.getX() != sprite.getOriginY()) {
			movementState = 1;
		} else if(!fired) {
			int rand = r.nextInt(5);
			if(rand == 0) {
				movementState = 3;
				targetPoint = new Point((int) (PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2), (int) (PlayScreen.player.sprite.getY() - PlayScreen.player.height / 2));
			} else {
				xSpeed = NewGame.BASE_SPEED;
				ySpeed = NewGame.BASE_SPEED;
				xAmp = (float) ((sprite.getX() - PlayScreen.player.sprite.getX()) / 2);
				yAmp = (PlayScreen.player.sprite.getY() - sprite.getY()) / 2;
				float fx = (float) (xAmp *  Math.cos(Math.PI) + sprite.getX() - xAmp * Math.cos(0)); 
				float fy = (float) (-yAmp *  Math.sin(Math.PI) - sprite.getY());
				tempPoint = new Point((int) sprite.getX(), (int) sprite.getY());
				targetPoint = new Point((int) fx, (int) fy);
				fallTheta = (float) (r.nextInt((int) DELTA_THETA) / 10 + DELTA_THETA / 4);
				fired = true;
			}
			if(rand == 1)  {
				nextMovementState = 4;
			} else {
				secondaryFire = false;
			}
		} else if(fired && Math.toDegrees(deltaTheta) < Math.toDegrees(DELTA_THETA)) {
			//			System.out.println(deltaTheta + ", " + fallTheta);
			if(nextMovementState == 4 && deltaTheta > fallTheta) {
				movementState = 4;
				restTime = 1;
				restCount = 0;
			} else { 
				deltaTheta += 1 * handler.deltaTime;
				sprite.setPosition((float) (xAmp *  Math.cos(deltaTheta) + tempPoint.getX() - xAmp * Math.cos(0)), (float) (yAmp *  Math.sin(deltaTheta) + tempPoint.getY()));
				if(deltaTheta > fallTheta && !secondaryFire) {
					handler.addList.add(new EnemyProjectile(Assets.findRegion("Acid"), sprite.getX() + width / 2, sprite.getY() + height, NewGame.BASE_SPEED * 1.5f, 0.25f, ObjectId.EnemyProjectile, handler));
					sounds.get(1).play(NewGame.gameVolume);
					secondaryFire = true;
				} 
			}
		} else { 
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

	public void action3() { // suicide towards player
		innerRectangleOnly = true;
		if(!secondaryFire) {
			xSpeed = NewGame.BASE_SPEED;
			ySpeed = NewGame.BASE_SPEED;
			calculateFlyVelocities();
			restTime = 1;
			restCount = 0;
			secondaryFire = true;
		}
		if(moveTo(targetPoint, Math.abs(xSpeed), Math.abs(ySpeed))) {
			deathAnim.runAnimation();
			sprite.setRegion(deathAnim.currentImg);
			if(deathAnim.getCount() == deathAnim.size() - 1) {
				handler.disposeList.add(this);
			}
		} 
	}
	
	// fall out of sky
	public void action4() {
		innerRectangleOnly =  true;
		sprite.setOriginCenter();
		sprite.rotate(270 * handler.deltaTime);
		velX = 0;
		velY += NewGame.BASE_SPEED * Gdx.graphics.getDeltaTime();
		hasGravity = true;
		if(onGround) {
			velY = 0;
			deathAnim.runAnimation();
			sprite.setRegion(deathAnim.currentImg);
			if(deathAnim.getCount() == deathAnim.size() - 1) {
				handler.disposeList.add(this);
			}
		}
	}

	protected void blockCollisions(GameObject tempObject) { 
		if (bottomBounds.overlaps(tempObject.boundingRectangle)) {
			//			sprite.setPosition(sprite.getX(), tempObject.sprite.getY() - height * 2);
			velY = 0;
			onGround = true;
		} 
	}

	public void drawSprite(SpriteBatch batch) { 
		if (hp > 0) {
			spiderlingFly.runAnimation();
			sprite.setRegion(spiderlingFly.currentImg);
		} else {
			sprite.setRegion(deathAnim.currentImg);
		}
		if(deathAnim.index > 0 || deathAnim.getCount() > 0 || takingDamage) {
			batch.setColor(Color.RED);
		}
		sprite.setFlip(sprite.isFlipX(), true);
		batch.draw(sprite, sprite.getX(), sprite.getY() - height, width, height);
		batch.setColor(Color.WHITE);
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY() - height, width,  height);
	}
}

