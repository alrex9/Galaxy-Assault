package objects;

import java.util.LinkedList;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;
import framework.PlayScreen;

public class SpinningDisk extends Enemy {

	public Animation spinBladeSpin;

	public SpinningDisk(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findRegion("SpinBladeIdle"),x, y, handler, id);
		scaleValue = 0.75f;
		hp = 777;
		alive = true; 
		hasGravity = true;
		maxStateNum = 1;
		minStateNum = 1;
		animations = new Animation[3];
		animations[2] = spinBladeSpin = Assets.findAnimation("SpinBlade");
		spinBladeSpin.setCount(0);
		animationState = 2;
		dealingDamage = false;
		animationState = 1;
		sounds.add(Assets.findSound("SpinningBlade.wav"));
		sounds.add(Assets.findSound("BatInjury,SpinBlade.wav"));
	}
	
	public void tick(LinkedList<GameObject> object) {
		if (this.sprite.getX() + width < handler.gameCam.position.x - NewGame.V_WIDTH / 2) {
			handler.disposeList.add(this);
		}
		super.tick(object);
	}

	public void action1() {
		spinBladeSpin.runAnimation();
		sprite.setRegion(spinBladeSpin.currentImg);
		if(!fired && Math.abs(PlayScreen.player.sprite.getX() + PlayScreen.player.width / 2 - sprite.getX() + width / 2) < NewGame.V_WIDTH / 3) {
			if (velX == 0) {
//				System.out.println("adding spin blade sound");
//				PlayScreen.playSounds.add(bladeSpinSound);
				sounds.get(0).play(NewGame.gameVolume);
				fired = true;
			}
			velX = -NewGame.BASE_SPEED * 1.5f;
		}
	}

	protected void blockCollisions(GameObject tempObject) { 
		if (bottomBounds != null && hp > 0 && bottomBounds.overlaps(tempObject.boundingRectangle)) {
			sprite.setPosition(sprite.getX(), tempObject.sprite.getY() + 1);
			velY = 0;
			falling = false;
			jumping = 0;
		} else if (bottomBounds.overlaps(tempObject.boundingRectangle)) {
			velY = 0;
		} else {
			falling = true;
		}
	}

	public void drawSprite(SpriteBatch batch) { 
		batch.draw(sprite, sprite.getX(), sprite.getY() - height, width, height);
		//		batch.draw(Assets.findRegion("BossBlock"), topBounds.getX(), topBounds.getY(), topBounds.getWidth(), topBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), bottomBounds.getX(), bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), leftBounds.getX(), leftBounds.getY(), leftBounds.getWidth(), leftBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), rightBounds.getX(), rightBounds.getY(), rightBounds.getWidth(), rightBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), constantDamageBox.getX(), constantDamageBox.getY(), constantDamageBox.getWidth(), constantDamageBox.getHeight());
		//		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY() - height, width,  height);
		//					batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
