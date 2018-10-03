package objects;

import java.awt.Point;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.NewGame.STATE;
import framework.ObjectId;
import framework.PlayScreen;

public class BossOne extends Boss{
	
	private final float CHARGE_CONSTANT = (float) (NewGame.BASE_SPEED * 1.5);
	private final int SPROUT_COUNT =  7;
	private final double DEGREES_BETWEEN = 8;
	private boolean singleFire, flipX;
	private int smashCount;
	public Animation bossStomp, bossSmash, bossLegsUp;

	/* Heavy Boss
	 * 3 attacks
	 * 	1) shockwave, maybe add a jump/stomp
	 * 	2) cluster of bouncing projectiless
	 *	3) charge
	 *
	 *	Points:
	 *		1) Start
	 */

	public BossOne(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findRegion("BossOneIdle"), x, y, handler, id);
		sprite.setSize((int) (NewGame.V_WIDTH / 4), (int) (NewGame.V_HEIGHT / 4.5));
		sprite.setY(sprite.getY() - 1);
		maxStateNum = 7;
		minStateNum = 2;
		animations = new Animation[5];
		hasGravity = true;
		animations[0] = bossRun = Assets.findAnimation("BossOneRun");
		bossRun.speed = 0.05f;
		animations[1] = deathAnim = Assets.findAnimation("BossOneDeath");
		deathAnim.speed = 0.25f;
		animations[2] = bossStomp = Assets.findAnimation("BossOneStompFeet");
		bossStomp.speed = 0.05f;
		animations[3] = bossSmash = Assets.findAnimation("BossOneButtSmash");
		bossSmash.speed = 0.05f;
		animations[4] = bossLegsUp = Assets.findAnimation("BossOneLegsUp");
		bossLegsUp.speed = 0.15f;
		animationState = -1;
		setScale(0.75f);
		nextMovementState = 3;
		maxHp = 3;
		hp = maxHp;
		singleFire = false;
		sounds.add(Assets.findSound("ButtSmashDeath.wav"));
		sounds.add(Assets.findSound("RobotWalking.wav"));
		sounds.add(Assets.findSound("RocksFalling,Charge.wav"));
		sounds.add(Assets.findSound("Smash.wav"));
		sounds.add(Assets.findSound("WindGust.wav"));
		sounds.add(Assets.findSound("WindHits.wav"));
	}
	
	public void tick(LinkedList<GameObject> object) {
		super.tick(object);
//		System.out.println(this.innerRectangleOnly + " " + this.dealingDamage);
	}

	public void action1() { // go to a location
		if(moveTo(targetPoint, Math.abs(xSpeed), Math.abs(ySpeed))) {
			animationState = -1;
			sprite.setRegion(idleFrame);
			reset();
		} //kinda suprised the code below this works
		bossRun.runAnimation(); 
		animationState = 0;
		sprite.setRegion(bossRun.currentImg);
	}

	public void action2() { // walk off the screen in the opposite direction of the player
		if(!fired) {
			bossRun.setCount(0);
			if(PlayScreen.player.sprite.getX() > sprite.getX()) {
				velX = 1.3f * CHARGE_CONSTANT;
				sprite.setFlip(false, sprite.isFlipY());
			} else {
				velX = -1.3f * CHARGE_CONSTANT;
				sprite.setFlip(true, sprite.isFlipY());
			}
			fired = true;
		} else { 
			hasGravity = false;
			if(sprite.getX() <= handler.gameCam.position.x - NewGame.V_WIDTH / 2 - width) { // left of the screen
				sprite.setX(handler.gameCam.position.x + NewGame.V_WIDTH / 2 + width);
				setPostChargeConditions();
			} else if(sprite.getX() >= handler.gameCam.position.x + NewGame.V_WIDTH / 2) { // right of the screen
				sprite.setX(handler.gameCam.position.x - NewGame.V_WIDTH / 2 - width);
				setPostChargeConditions();
			} else {
				bossRun.runAnimation();
				animationState = 0;
				sprite.setRegion(bossRun.currentImg);
			}
		}
	}

	private void setPostChargeConditions() {
		xSpeed = WALK_CONSTANT;
		targetPoint = new Point((int) (handler.gameCam.position.x - sprite.getWidth() / 2), 0);
		fired = false;
		movementState = 1;
		bossRun.setCount(0);
		sprite.setFlip(sprite.isFlipX(), false);
	}

	public void action3() { // fire shockwave
		hasGravity = true;
		bossSmash.runAnimation();
		animationState = 3;
		sprite.setRegion(bossSmash.currentImg);
		if(!secondaryFire) {
			secondaryFire = true;
		}
		if(bossSmash.getCount() == bossSmash.size() / 2 + 2) {
			handler.gameCam.position.set(initialCamX, initalCamY, 0);
		} else {
			initialCamX = handler.gameCam.position.x;
			initalCamY = handler.gameCam.position.y;
		}
		if(bossSmash.getCount() == bossSmash.size() / 2 || bossSmash.getCount() == bossSmash.size() / 2 - 1 || bossSmash.getCount() == bossSmash.size() / 2 + 1) {
			handler.gameCam.position.set(handler.gameCam.position.x + r.nextInt(5) - 2, handler.gameCam.position.y + r.nextInt(5) - 2, 0);
		}
		if(bossSmash.getCount() == bossSmash.size() / 2 && !fired) {
//			System.out.println("adding shockwave");
			sounds.get(3).play(NewGame.gameVolume);
			sounds.get(4).play(NewGame.gameVolume);
			handler.addList.add(new Shockwave(sprite.getX() + width / 2, (float) (sprite.getY()), ObjectId.EnemyProjectile, handler, 0));
			handler.addList.add(new Shockwave(sprite.getX(), (float) (sprite.getY()), ObjectId.EnemyProjectile, handler, 180));
			fired = true;
		}
		if(fired && bossSmash.getCount() == 0) {
			reset();
			bossSmash.setCount(0);
			sounds.get(3).stop();
			sounds.get(4).stop();
		}
	}

	public void action4() { // fire sprout
		//		if(animations[movementState].getCount() == animations[movementState].size()  / 2 && !fired) { // probably add something that decides what the middle picture of the animation is and fires on that
		//			for(int i = 0; i < SPROUT_COUNT; i++) {
		//				handler.addList.add(new BouncingProjectile(sprite.getX() + (textures.enemy[0].getRegionWidth() / 2), sprite.getY(), ObjectId.BouncingProjectile, handler, (i * DEGREES_BETWEEN) - (SPROUT_COUNT * DEGREES_BETWEEN) / 2 - 90)); // i * degreesbetween - totalrange (in degrees) / 2
		//			}
		//			fired = true;
		//		}
		//		if(fired && animations[movementState].getCount() == 0) {
		//			fired = false;
		//			runningScript = false;
		//		}
		reset();
	}

	public void action5() {
		handler.gameCam.position.set(handler.gameCam.position.x + r.nextInt(7) - 3, handler.gameCam.position.y + r.nextInt(7) - 3, 0);
		if(!fired) {
			sounds.get(2).play(NewGame.gameVolume);
			smashCount = r.nextInt(SPROUT_COUNT) + 1;
			fired = true;
			initialCamX = handler.gameCam.position.x;
			initalCamY = handler.gameCam.position.y;
		}
		if(smashCount > 0) { 
			bossStomp.runAnimation();
			animationState = 2;
			sprite.setRegion(bossStomp.currentImg);
			if(bossStomp.getCount() == bossStomp.size() / 2 && !secondaryFire) {
				secondaryFire = true;
				int rockCount = r.nextInt(5) + 10;
				for(int i = 0; i < rockCount; i++){
					String region = "rock" + (r.nextInt(2) + 1);
					FallingRock temp = new FallingRock(Assets.findRegion(region), handler.gameCam.position.x - NewGame.V_WIDTH / 2 + (NewGame.V_WIDTH * .1f) + r.nextInt((int) (NewGame.V_WIDTH / 1.5777f)), handler.gameCam.position.y - NewGame.V_HEIGHT / 2 - NewGame.V_HEIGHT / 10, (int) NewGame.V_WIDTH / 3, (int) NewGame.V_HEIGHT / 3, ObjectId.EnemyProjectile, handler, 270);
					temp.sprite.setOriginCenter();
					temp.sprite.rotate(r.nextInt(360));
					handler.addList.add(temp);
				}
			} else if(bossStomp.getCount() > (bossStomp.size() / 2) + 1 && secondaryFire) {
				secondaryFire = false;
				smashCount--;
				handler.gameCam.position.set(initialCamX, initalCamY, 0);
			}
		} else {
			sounds.get(2).stop();
			reset();
		}
	}

	public void action6() {
		if(restCount == 0.5f) {
			bossLegsUp.runAnimation();
		}
		if(!secondaryFire) {
			if(sprite.getX() + width / 2 < PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2) {
				flipX = true;
			} else {
				flipX = false;
			}
			animationState = 4;
			innerRectangleOnly = true;
			dealingDamage = false;
			smashCount = r.nextInt(2) + 3;
			restCount = 0.5f;
			secondaryFire = true;
		}
		if(bossLegsUp.getCount() == bossLegsUp.size() - 2 && !fired) {
			this.innerRectangleOnly = true;
			smashCount--;
			if (smashCount <= 0) {
				fired = true;
				bossLegsUp.setCount(0);
			} else {
				bossLegsUp.setCount(1);
			}
		}
		if(fired) {
			bossLegsUp.setCount(0);
			bossLegsUp.currentImg = bossLegsUp.images[bossLegsUp.getCount()];
			if(restCount >= 0) {
				restCount -= Gdx.graphics.getDeltaTime();
				handler.gameCam.position.set(handler.gameCam.position.x + r.nextInt(7) - 3, handler.gameCam.position.y + r.nextInt(7) - 3, 0);
			} else {
				sounds.get(3).play(NewGame.gameVolume);
				handler.gameCam.position.x = this.initialCamX;
				handler.gameCam.position.y = this.initalCamY;
				this.innerRectangleOnly = false;
				reset();
			}
		}
		sprite.setRegion(bossLegsUp.currentImg);
	}

	public void action7() {
		targetPoint = new Point((int) (handler.gameCam.position.x - NewGame.V_WIDTH / 2.1f + r.nextInt((int) (NewGame.V_WIDTH / 1.2f - idleFrame.getRegionWidth() * scaleValue))), 0); 
		movementState = 1;
		restCount = 2;
	}

	public void draw(SpriteBatch batch) {
		if(movementState == 0) {
			width = idleFrame.getRegionWidth() * scaleValue;
			height = idleFrame.getRegionHeight() * scaleValue;
			sprite.setRegion(idleFrame);
			animationState = -1;
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

	protected void blockCollisions(GameObject tempObject) {
		if(!singleFire && NewGame.State == STATE.TRANSITIONTOBOSS) {
			hasGravity = false;
			velY = 0;
			gravity = 0;
			bottomBounds = null;
			rightBounds = null;
			leftBounds = null;
			sprite.setY(sprite.getY() + Handler.BLOCK_HEIGHT / 2);
			singleFire = true;
		} else if (NewGame.State == STATE.GAME && hp > 0 && bottomBounds.overlaps(tempObject.boundingRectangle)) {
			sprite.setPosition(sprite.getX(), tempObject.sprite.getY() + 1);
			velY = 0;
			falling = false;
			jumping = 0;
		}

	}

	public void physics(LinkedList<GameObject> object) {
		if (!thirdFired && animationState == 0) {
			thirdFired = true;
			sounds.get(1).loop(NewGame.gameVolume);
		} else if (animationState != 0) {
			thirdFired = false;
			sounds.get(1).stop();
		}
		if(singleFire) {
			velY = 0;
		}
		move();
		if ((falling || jumping == 0) && hasGravity && !singleFire) {
			velY += gravity * handler.deltaTime;
			if (velY > MAX_SPEED) {
				velY = MAX_SPEED;
			}
		} else {
			velY = 0;
		}
		Collision(object);
		takeDamage();
	}

//	protected void playerCollisions() {
//		if(!PlayScreen.player.takingDamage && this.dealingDamage&& (PlayScreen.player.boundingRectangle.overlaps(this.constantDamageBox) || (PlayScreen.player.boundingRectangle.overlaps(this.boundingRectangle)))) {
//			PlayScreen.player.takingDamage = true;
//			//			PlayScreen.player.hp--; // add back in to make player take damage
//		} 
//		if (PlayScreen.player.dashing && !this.takingDamage && PlayScreen.player.boundingRectangle.overlaps(this.boundingRectangle)) {
//			this.takingDamage = true;
//			this.hp--;
//		}
//	}
}
