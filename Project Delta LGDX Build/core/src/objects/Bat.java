package objects;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import framework.Animation;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;
import framework.PlayScreen;

public class Bat extends Enemy{

	//	public final float FLOAT_RANGE = NewGame.V_WIDTH / 7;
	public final float ACCELY = NewGame.BASE_SPEED  / 7;

	public Animation batFly;

	public Bat(float x, float y, Handler handler, ObjectId id) {
		super(x, y, handler, id);
		maxStateNum = 1;
		minStateNum = 1;
		animations = new Animation[2];
		animations[0] = batFly = Assets.findAnimation("BatFly");
		animations[1] = deathAnim = Assets.findAnimation("BatDeathAnim");
		animations[1].speed = 0.075f;
		animations[0].setCount(0);
		animations[1].setCount(0);
		velY = -NewGame.BASE_SPEED / 7;
		hasGravity = false;
		sounds.add(Assets.findSound("BatFlying.wav"));
		sounds.add(Assets.findSound("BatHits.wav"));
		sounds.add(Assets.findSound("BatInjury,SpinBlade.wav"));
		sounds.get(0).loop(NewGame.gameVolume);
		
	}

	public void tick(LinkedList<GameObject> object) {
		if (this.sprite.getX() + width / 2 < handler.gameCam.position.x - NewGame.V_WIDTH / 2) {
			handler.disposeList.add(this);
		}
		super.tick(object);
	}

	public void action1() {
		animations[0].runAnimation();
		sprite.setRegion(animations[0].currentImg);
		if (sprite.getY() < sprite.getOriginY()) {
			velY += ACCELY * Gdx.graphics.getDeltaTime();
		} else { 
			velY -= ACCELY * Gdx.graphics.getDeltaTime();
		}
	}

	public void actionDeath() {
		super.actionDeath();
		if(!fired) {
			sounds.get(2).play(NewGame.gameVolume);
		}
	}

	protected void playerCollisions() {
		if(!PlayScreen.player.takingDamage && !PlayScreen.player.dashing && hp > 0 &&  ((PlayScreen.player.boundingRectangle.overlaps(this.constantDamageBox) && !innerRectangleOnly) || (PlayScreen.player.boundingRectangle.overlaps(this.boundingRectangle) && this.dealingDamage))) {
			PlayScreen.player.takingDamage = true;
			sounds.get(2).play(NewGame.gameVolume);
			//			PlayScreen.player.hp--; // add back in to make player take damage
		} 
		if (PlayScreen.player.dashing && !this.takingDamage && PlayScreen.player.boundingRectangle.overlaps(this.boundingRectangle)) {
			this.takingDamage = true;
			this.hp--;
			sounds.get(1).play(NewGame.gameVolume);
		}
	}

	protected void blockCollisions(GameObject tempObject) { 
	}
	
	public void dispose() {
		super.dispose();
		sounds.get(0).stop();
	}
}
