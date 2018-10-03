package objects;

import java.awt.Point;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.NewGame.STATE;
import framework.ObjectId;
import framework.PlayScreen;

/* Spider boss which climbs on walls
 * 3 attacks
 * 	1) fall from ceiling onto player
 * 	2) melee attack / rethink (maybe shoot webs (potentially slowing player/doing damage)) 
 *	3) spiderlings
 *
 * 	Points:
 * 		1) Start
 * 		2) middle ceiling position where enemy rests
 */

public class BossTwo extends Boss {

	private int spitCount;
	private boolean flipX, flipY;
	public Animation bossStab, bossUpsideDownWalk, bossSpit;

	public BossTwo(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findRegion("BossTwoIdle"), x, y - Assets.findAnimation("BossTwoDeath").images[0].getRegionHeight(), handler, id);
		hasGravity = true;
		//		maxSpeed = NewGame.BASE_SPEED * 3;
		maxStateNum = 3;
		minStateNum = 2;
		restTime = 1;
		animations = new Animation[5];
		animations[0] = bossRun = Assets.findAnimation("BossTwoWalk");
		bossRun.speed = 0.05f;
		animations[1] = deathAnim = Assets.findAnimation("BossTwoDeath");
		deathAnim.speed = .25f;
		animations[2] = bossStab = Assets.findAnimation("BossTwoStab");
		bossStab.speed = .075f;
		animations[3] = bossUpsideDownWalk = Assets.findAnimation("BossTwoUpsideDownWalk");
		bossUpsideDownWalk.speed = .05f;
		animations[4] = bossSpit = Assets.findAnimation("BossTwoSpit");
		bossSpit.speed = .05f;
		flipX = false;
		flipY = true;
		setScale(0.75f);
		maxHp = 3;
		hp = maxHp;
		sounds.add(Assets.findSound("BugRelease.wav"));
		sounds.add(Assets.findSound("PoisonShot.wav"));
		sounds.add(Assets.findSound("SpoderGetsHit.wav"));
		sounds.add(Assets.findSound("SpyderDeath.wav"));
	}
	protected void specificStart() {
		points.add(new Point((int) sprite.getOriginX() , 0));
	}

	public void tick(LinkedList<GameObject> object) { // eventually to be removed
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

	public void action1() { // go to a location
		animationState = 0;
		if(targetPoint.getX() > sprite.getX()) {
			flipX = true;
		} else {
			flipX = false;
		}
		if(moveTo(targetPoint, Math.abs(xSpeed), Math.abs(ySpeed))) {
			reset();
		} else {
			animationState = 0;
			if(flipY) {
				bossRun.runAnimation(); 
				sprite.setRegion(bossRun.currentImg);
			} else {
				bossUpsideDownWalk.runAnimation();
				sprite.setRegion(bossUpsideDownWalk.currentImg);
			}
		}
	} 

	public void action2() { // move to ceiling
		animationState = 0;
		if(PlayScreen.player.sprite.getX() < sprite.getX() && !fired) {
			velX = (float) (NewGame.BASE_SPEED * 1.5);
			flipX = true;
			fired = true;
		} else if(!fired) {
			velX = (float) (-NewGame.BASE_SPEED * 1.5);
			flipX = false;
			fired = true;
		} else if(sprite.getX() <= handler.gameCam.position.x - NewGame.V_WIDTH / 2 - width * 2) { // left of the screen
			sprite.setX(handler.gameCam.position.x - NewGame.V_WIDTH / 2 - width);
			flipX = false;
			setWalkConditions();
		} else if(sprite.getX() - width * 2 > handler.gameCam.position.x + NewGame.V_WIDTH / 2) { // right of the screen
			sprite.setX(handler.gameCam.position.x + NewGame.V_WIDTH / 2);
			flipX = true;
			setWalkConditions();
		}
		bossRun.runAnimation(); 
		animationState = 0;
		sprite.setRegion(bossRun.currentImg);
	}
	private void setWalkConditions() { // sets up ceiling attacks
		sprite.setY(handler.gameCam.position.y - NewGame.V_HEIGHT / 2 + height);
		flipY = false;
		fired = false;
		gravity = 0;
		hasGravity = false;
		movementState = 1;
		xSpeed = NewGame.BASE_SPEED;
		targetPoint = points.get(0); 
		maxStateNum = 5; // needs to be changed to 5
		minStateNum = 4; // needs to be changed to 4
		bossRun.setCount(0);
		bossUpsideDownWalk.setCount(0);
	}

	// stab attack
	public void action3() {
		if(!fired) {
			animationState = 0;
			hasGravity =  false;
			targetPoint = new Point((int) (PlayScreen.player.sprite.getX() + (PlayScreen.player.width / 2)), 0);
			flipX = false;
			if(PlayScreen.player.sprite.getX() > sprite.getX()) {
				targetPoint.x -= width * 1.2f;
				flipX = true;
			}
			bossRun.setCount(0);
			bossStab.setCount(0);
			fired = true;
		} else if(Math.abs(sprite.getX() + width / 2 - targetPoint.getX())  < NewGame.V_WIDTH / 5) {
			secondaryFire = true;
			if (bossStab.getCount() == bossStab.size() - 1) {
				reset();
			} else {
				bossStab.runAnimation(); 
			}
			sprite.setRegion(bossStab.currentImg);
			animationState = 2;
		} else if (!secondaryFire) {
			bossRun.runAnimation(); 
			animationState = 0;
			sprite.setRegion(bossRun.currentImg);
		}
		moveTo(targetPoint, (float) (NewGame.BASE_SPEED * 1.5), 0);
	}

	// fire from ceiling (uses rest count variable to simulate a timer)
	public void action4() { 
		if(!fired) {
			bossSpit.setCount(0);
			spitCount = 0;
			fired =true;
			animationState = 4;
		}
		int tempCount = bossSpit.getCount();
		bossSpit.runAnimation();
		sprite.setRegion(bossSpit.currentImg);
		animationState = 4;
		if(bossSpit.getCount() == 7 && !secondaryFire) {
			sounds.get(1).play(NewGame.gameVolume);
			handler.addList.add(new EnemyProjectile(Assets.findRegion("Acid"), sprite.getX() + width / 2, sprite.getY(), NewGame.BASE_SPEED * 2, 0.5f, ObjectId.EnemyProjectile, handler));	
			secondaryFire = true;
			spitCount++;
		}
		if (bossSpit.getCount() == 8) {
			secondaryFire = false;
		}
		if(tempCount > bossSpit.getCount() && spitCount > r.nextInt(2) + 5) {
			if (r.nextInt(2) == 0) {
				movementState = 6; 
			} else {
				reset();
			}
		}
	}

	public void action5() { // spawn minions from ceiling 
		if(!fired) {
			animationState = 4;
			bossSpit.setCount(0);
			bossSpit.speed = 0.1f;
			spitCount = 0;
			fired = true;
		}
		bossSpit.runAnimation();
		if (bossSpit.getCount() == 6) {
			bossSpit.setCount(4);
			bossSpit.index = 0;
			spitCount++;
		}
		sprite.setRegion(bossSpit.currentImg);;
		if(spitCount > 5) {
			sounds.get(0).play(NewGame.gameVolume);
			handler.addList.add(new Spiderling(handler.gameCam.position.x - NewGame.V_WIDTH / 2.1f, handler.gameCam.position.y - NewGame.V_WIDTH / 4, handler, id)); 
			handler.addList.add(new Spiderling(handler.gameCam.position.x + NewGame.V_WIDTH / 2.1f, handler.gameCam.position.y - NewGame.V_WIDTH / 4, handler, id)); 
			restTime = 2;
			if (r.nextInt(2) == 0) {
				movementState = 6; 
			} else {
				reset();
			}
		}
	}

	public void action6() { // find and fall to player
		if(!secondaryFire) {
			bossUpsideDownWalk.runAnimation(); 
			animationState = 3;
			sprite.setRegion(bossUpsideDownWalk.currentImg);
			if(targetPoint.getX() > sprite.getX()) {
				flipX = true;
			} else {
				flipX = false;
			}
			targetPoint = new Point((int) PlayScreen.player.sprite.getX(), (int) PlayScreen.player.sprite.getY());
			moveTo(targetPoint, Math.abs(WALK_CONSTANT),0);
			if(sprite.getX() - width / 2 < PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2  && sprite.getX() + width / 2 > PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2) {
				fallToFloor();
			} 
		} else {
			if(velY == 0) {
				reset();
			} else {
				velY += gravity * handler.deltaTime;
			}
		}
	}
	private void fallToFloor() { // falls on to the players previous x position
		sprite.setRegion(idleFrame); // fall animation or image
		animationState = -1;
		velY = NewGame.BASE_SPEED / 4;
		velX = 0;
		gravity = NewGame.BASE_SPEED * 3;
		maxStateNum = 3;
		minStateNum = 2;
		secondaryFire = true;
		flipY = true;
	}

	public void draw(SpriteBatch batch) {
		if(movementState == 0) {
			if (flipY) {
				sprite.setRegion(idleFrame);
				width = idleFrame.getRegionWidth() * scaleValue;
				height = idleFrame.getRegionHeight() * scaleValue;
			} else {
				sprite.setRegion(Assets.findRegion("BossTwoHangingIdle"));
				width = sprite.getRegionWidth() * scaleValue;
				height = idleFrame.getRegionWidth() * scaleValue;
			}
			animationState = -1;
		}
		sprite.setFlip(flipX, flipY);
		drawSprite(batch);
	}

	public void drawSprite(SpriteBatch batch) { 
		if(!playDamagedSound && takingDamage) { 
			playDamagedSound = true;
			sounds.get(2).play(NewGame.gameVolume);
		}
		if(takingDamage) {
			batch.setColor(Color.RED);
		}
		if (hp <= 0) {
			if(alive) {
				sounds.get(2).play(NewGame.gameVolume);
			}
			alive = false;
		}
		sprite.setFlip(flipX, flipY);
		batch.draw(sprite, sprite.getX(), sprite.getY() - height, width, height);
		batch.setColor(Color.WHITE);
		float barDrawHeight = sprite.getY() - height - 35;
		if(!flipY) {
			barDrawHeight = sprite.getY() + 35;
		}
		if(hp == maxHp) {
			batch.draw(Assets.findRegion("EnemyHealthBarFull"), sprite.getX() + width / 2 - 350 / 2, barDrawHeight, 350, 35);
		} else if(hp > 0) {
			batch.draw(Assets.findRegion("EnemyHealthBarReduced"), sprite.getX() + width / 2 - 350 / 2, barDrawHeight, 350, 35);
		} else {
			batch.draw(Assets.findRegion("EnemyHealthBarEmpty"), sprite.getX() + width / 2 - 350 / 2, barDrawHeight, 350, 35);
		}
		if(hp > 0){
			if(!flipY) {
				batch.draw(Assets.findRegion("EnemyHealthBarOnly"), sprite.getX() + width / 2 + 45 - 350 / 2, barDrawHeight + 3, 250 * hp / maxHp, 27);
			} else {
				batch.draw(Assets.findRegion("EnemyHealthBarOnly"), sprite.getX() + width / 2 + 45 - 350 / 2, barDrawHeight + 3, 250 * hp / maxHp, 27);
			}
		}
		//		batch.draw(Assets.findRegion("BossBlock"), topBounds.getX(), topBounds.getY(), topBounds.getWidth(), topBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), bottomBounds.getX(), bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), leftBounds.getX(), leftBounds.getY(), leftBounds.getWidth(), leftBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), rightBounds.getX(), rightBounds.getY(), rightBounds.getWidth(), rightBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), constantDamageBox.getX(), constantDamageBox.getY(), constantDamageBox.getWidth(), constantDamageBox.getHeight());
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY() - height, width,  height);
		//					batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}

	protected boolean newScriptParameters() {
		return NewGame.State == activeState && !runningScript && restCount > restTime;
	}

	protected void calculateBounds() {
		if (hp <= 0) {
			bottomBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height / 2, width / 2, height / 4);
			//			sprite.setY(sprite.getY() + NewGame.BASE_SPEED * Gdx.graphics.getDeltaTime());
			hasGravity = true;
			rightBounds = null;
			leftBounds = null; 
		} else { 
			bottomBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height + (height * 3 / 4), width / 2, height / 4);
			leftBounds = new Rectangle( sprite.getX(), sprite.getY() - height + height  / 24, width / 4, height / 1.2f);
			rightBounds = new Rectangle(sprite.getX() + width * 3 / 4, sprite.getY() - height + height / 24, width / 4, height / 1.2f);
		}
		topBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height, width / 2, idleFrame.getRegionHeight() * scaleValue / 4);
		constantDamageBox = new Rectangle(sprite.getX() + width / 4, sprite.getY() - height + height / 4, width / 2, height / 2);
	}
}
