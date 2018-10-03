package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import framework.Assets;
import framework.NewGame;
import framework.PlayScreen;

public class AttackButton extends HUDObject {

	public boolean pressed;

	public AttackButton(float x, float y, boolean clickable, String... args) {
		super(x, y, clickable, args);
		width = 150;
		height = 150;
		super.universalConstructor(x, y, clickable, args);
	}

	public void drawSprite(SpriteBatch batch) { 
		int temp = 0;
		if(PlayScreen.player.attackState != 0) { 
			temp = 1;	
		}
		batch.draw(Assets.findRegion(names[temp]), x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width, -height);
		//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX() + PlayScreen.PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, boundingRectangle.getY() + PlayScreen.PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}

	public boolean checkInput(Vector2 input) {
		if(boundingRectangle.contains((Vector2) input) && (PlayScreen.player.attackState == 0 ||
				PlayScreen.player.playerAttackOne.getCount() > (PlayScreen.player.playerAttackOne.size() - PlayScreen.player.playerAttackOne.size() / 5) || 
				PlayScreen.player.playerAttackTwo.getCount() > PlayScreen.player.playerAttackTwo.size() - PlayScreen.player.playerAttackTwo.size() - 5)) {
			System.out.println("Attacking");
			pressed = true;
			return true;
		} else {
			index = 0;
			pressed = false;
			return false;
		}
	}
	public void pressed() {
		if(PlayScreen.player.attackState == 0) {
			if(PlayScreen.player.getJumping() < 1) {
				PlayScreen.player.setVelX(0);
			}
			PlayScreen.player.attackState = 1;
			PlayScreen.player.playerAttackOne.setCount(0);
			PlayScreen.player.playerJumpAttack.setCount(0);
		} 	else if(PlayScreen.player.attackState == 1 && PlayScreen.player.playerAttackOne.getCount() > (int) (PlayScreen.player.playerAttackOne.size() - PlayScreen.player.playerAttackOne.size() / 5)) {
			PlayScreen.player.setVelX(0);
			PlayScreen.player.attackingNext = true;
			PlayScreen.player.playerAttackTwo.setCount(0);
		} else if(PlayScreen.player.attackState == 2 && PlayScreen.player.playerAttackTwo.getCount() > (int) (PlayScreen.player.playerAttackTwo.size() - PlayScreen.player.playerAttackTwo.size() - 5)) {
			PlayScreen.player.setVelX(0);
			PlayScreen.player.attackingNext = true;
			PlayScreen.player.playerSlash.setCount(0);
		} 
	}
}
