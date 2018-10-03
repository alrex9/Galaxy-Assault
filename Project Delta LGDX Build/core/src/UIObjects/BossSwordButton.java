package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.NewGame;
import framework.NewGame.STATE;
import framework.PlayScreen;
import objects.BossSword;

public class BossSwordButton extends HUDObject {

	private boolean pressed;
	public static float bossX;

	public BossSwordButton(float x, float y, boolean clickable, String... args) {
		super(x, y, clickable, args);
		System.out.println("added boss sword button");
		width = 200;
		height = 150;
	}


	public void drawSprite(SpriteBatch batch) { 
		super.drawSprite(batch);
		if(NewGame.State == STATE.GAME || NewGame.State == STATE.TRANSITIONFROMBOSS) {
			PlayScreen.hud.elements.remove(this);
		}
	}
	public void pressed() {
		if (!pressed) {
			super.pressed();
			NewGame.State = STATE.BOSSSWORD;
			PlayScreen.player.handler.addList.add(new BossSword(null, PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH * 1.5f, 
					PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT  / 2, PlayScreen.player.handler, null));
			pressed = true;
		}
	}
}
