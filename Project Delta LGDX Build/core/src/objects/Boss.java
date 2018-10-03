package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.NewGame.STATE;
import framework.ObjectId;

/*BOSS CLASS TYPES:
 *  Spider boss which climbs on walls
 * 3 attacks
 * 	1) fall from ceiling onto player
 * 	2) melee attack / rethink (maybe shoot webs (potentially slowing player/doing damag)) 
 *	3) spiderlings
 * needs to have a stage where it can take damage (either projectile or "stomp" damage
 * should have some sort of movement pattern that potentially changes based on HP
 * needs some form of HP and way to show injury/different stages of fight
 *  
 * Heavy Boss
 * 3 attacks
 * 	1) shockwave, maybe add a jump/stomp
 * 	2) cluster of bouncing projectiles
 *	3) Charge
 *
 * Flying Boss: uses an array of locations to designate where to fly to in the sky
 * 3 attacks
 * 	1) fly by firing attack
 * 	2) targeted firing/missile (slow enough tracking to fly by and dodge)
 *	3) cluster bomb barrage 
 *	4) charge
 *
 * Sword Boss
 * 3 attacks
 * 	1) sword charge
 * 	2) minions
 *	3) heavy attack (leave him vaunerable)
 *
 * Wizard Boss: uses an array of locations to designate where to fly to in the sky
 * 	1) teleport
 * 	2) minions (skeletons)
 *	3) heavy "charge up" attack (leave him vaunerable)
 *	4) magical spreadshot
 * floats above the ground/flys
 * 
 * Scorpion Boss
 *  1) goes underground and strikes from behind/pops up from underground
 *  2) tail strike
 *  3) poison projectile from tail
 *  
 *  NOTE ON RUNNING "SCRIPT": for a lot of the actions it appears that the best way
 *	to do it is to stop running the current action when it reaches the end of the last animation.
 *  
 */

public class Boss extends Enemy {

	protected Animation bossRun;
	public float initialCamX, initalCamY;
	public int maxHp;
	public static boolean autoKill;
	public static Vector2 centerOfMass;
	public Boss(TextureRegion idleFrame, float x, float y, Handler handler, ObjectId id) {
		super(idleFrame, x, y, handler, id);
		activeState = STATE.BOSSFIGHT;
		setVariables();
		sprite.setFlip(sprite.isFlipX(), true);
		animationState = 0;
		autoKill = false;
		handler.bossCount++;
	}

	public void tick(LinkedList<GameObject> object) {
		super.tick(object);
		centerOfMass = new Vector2(sprite.getX() + (idleFrame.getRegionWidth() * scaleValue) / 2, sprite.getY() + (idleFrame.getRegionHeight() * scaleValue) / 2);
		if(autoKill) {
			hp = 0;
		}
		if (NewGame.State == STATE.BOSSSWORD) {
			velX = 0;
		}
	}

	protected void setVariables() {
		xSpeed = NewGame.BASE_SPEED;
		ySpeed = 0;
		//		points.add(0, new Point((int) PlayScreen.player.sprite.getOriginX(), (int) PlayScreen.player.sprite.getOriginY()));
		//		targetPoint = points.get(0);
		specificStart();
		variablesSet = true;
	}

	public void reset() { // sets the state to idle and not running a script
		movementState = 0;
		runningScript = false;
		fired = false;
		secondaryFire = false;
		bossRun.setCount(0);
	}

	protected void specificStart() {}

	public void drawSprite(SpriteBatch batch) { 
		if(takingDamage) {
			batch.setColor(Color.RED);
		}
		batch.draw(sprite, sprite.getX(), sprite.getY() - height, width, height);
		batch.setColor(Color.WHITE);
		if(hp == maxHp) {
			batch.draw(Assets.findRegion("EnemyHealthBarFull"), sprite.getX() + width / 2 - 350 / 2, sprite.getY() - height - 35, 350, 35);
		} else if(hp > 0) {
			batch.draw(Assets.findRegion("EnemyHealthBarReduced"), sprite.getX() + width / 2 - 350 / 2, sprite.getY() - height - 35, 350, 35);
		} else {
			batch.draw(Assets.findRegion("EnemyHealthBarEmpty"), sprite.getX() + width / 2 - 350 / 2, sprite.getY() - height - 35, 350, 35);
		}
		if(hp > 0){
			batch.draw(Assets.findRegion("EnemyHealthBarOnly"), sprite.getX() + width / 2 + 48 - 350 / 2, sprite.getY() - height - 32, 250 * hp / maxHp, 27);
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
}