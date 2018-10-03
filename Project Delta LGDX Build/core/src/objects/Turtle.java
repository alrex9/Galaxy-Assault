package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;
import framework.PlayScreen;

public class Turtle extends Enemy {

	public Animation turtleStab, turtleBuck;

	public Turtle(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findRegion("TurtleIdle"), x, y, handler, id);
		scaleValue = 0.75f;
		hp = 777;
		alive = true; 
		hasGravity = true;
		maxStateNum = 1;
		minStateNum = 1;
		animations = new Animation[4];
		animations[2] = turtleStab = Assets.findAnimation("TurtleStab");
		animations[3] = turtleBuck = Assets.findAnimation("TurtleBuck");
		turtleBuck.speed = 0.01777f;
		turtleStab.setCount(0);
		turtleBuck.setCount(0);
		animationState = -1;
		sounds.add(Assets.findSound("TurtleBuck.wav"));
		sounds.add(Assets.findSound("TurtleHits.wav"));
	}
	
	public void tick(LinkedList<GameObject> object) {
		if (this.sprite.getX() + width / 2 < handler.gameCam.position.x - NewGame.V_WIDTH / 2) {
			handler.disposeList.add(this);
		}
		super.tick(object);
	}

	public void action1() {
		if(animationState == 3) {
			if(!thirdFired) {
				thirdFired = true;
				sounds.get(0).play(NewGame.gameVolume);
			}
			animationState = 3;
			turtleBuck.runAnimation();
			sprite.setRegion(turtleBuck.currentImg);
			if (turtleBuck.getCount() == turtleBuck.size() - 1) {
				reset();
				animationState = -1;
			}
		} else if(Math.abs(PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2 - sprite.getX() + width / 2) < NewGame.V_WIDTH / 7) {
			turtleStab.runAnimation();
			sprite.setRegion(turtleStab.currentImg);
			animationState = 2;
			if (turtleStab.getCount() == turtleStab.size() - 1) {
				reset();
				animationState = -1;
			}
		} else {
			reset();
			restCount = 7;
			thirdFired = false;
		}
	}

	protected void playerCollisions() {
		if (PlayScreen.player.boundingRectangle.overlaps(this.topBounds)) {
			PlayScreen.player.setVelY(-NewGame.BASE_SPEED * 1.3777f);
			PlayScreen.player.jumping = 0;
			animationState = 3;
			movementState = 1;
			restCount = 7;
			turtleBuck.setCount(0);
		} else if (!PlayScreen.player.dashing && !this.takingDamage && animationState == 2 && ((PlayScreen.player.boundingRectangle.overlaps(this.rightBounds) && sprite.isFlipX()) || (PlayScreen.player.boundingRectangle.overlaps(this.leftBounds) && !sprite.isFlipX()))) {
			PlayScreen.player.hp = 0;
			sounds.get(1).play(NewGame.gameVolume);
		} 
	}

	public void drawSprite(SpriteBatch batch) { 
		if(animationState != 3 && PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2 < sprite.getX() + width / 2) {
			sprite.setFlip(false, true);
		} else {
			sprite.setFlip(true, true);
		}
		batch.draw(sprite, sprite.getX(), sprite.getY() - height, width, height);
		//		batch.draw(Assets.findRegion("BossBlock"), topBounds.getX(), topBounds.getY(), topBounds.getWidth(), topBounds.getHeight());
		//				batch.draw(Assets.findRegion("BossBlock"), bottomBounds.getX(), bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), leftBounds.getX(), leftBounds.getY(), leftBounds.getWidth(), leftBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), rightBounds.getX(), rightBounds.getY(), rightBounds.getWidth(), rightBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), constantDamageBox.getX(), constantDamageBox.getY(), constantDamageBox.getWidth(), constantDamageBox.getHeight());
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY() - height, width,  height);
		//					batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
	
	protected void calculateBounds() {
		topBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height + height / 5, width / 2, idleFrame.getRegionHeight() * scaleValue / 4);
		bottomBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height + (height * 3 / 4), width / 2, height / 4);
		leftBounds = new Rectangle( sprite.getX(), sprite.getY() - height + height  / 24, width / 4, height / 1.2f);
		rightBounds = new Rectangle(sprite.getX() + width * 3 / 4, sprite.getY() - height + height / 24, width / 4, height / 1.2f);
		constantDamageBox = new Rectangle(sprite.getX() + width / 4, sprite.getY() - height + height / 4, width / 2, height / 2);
	}
}
