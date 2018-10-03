package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import framework.Assets;
import framework.NewGame;
import framework.PlayScreen;

public class DashButton extends HUDObject {

	public boolean pressed;
	
	public DashButton(float x, float y, boolean clickable, String... args) {
		super(x, y, clickable, args);
		width = 150;
		height = 150;
		super.universalConstructor(x, y, clickable, args);
	}
	
	public void drawSprite(SpriteBatch batch) { 
		int temp = 0;
		if(pressed) { 
			temp = 1;	
		}
		batch.draw(Assets.findRegion(names[temp]), x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width, -height);
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX() + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, boundingRectangle.getY() + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
	
	public boolean checkInput(Vector2 input) {
		if(boundingRectangle.contains((Vector2) input)) {
			System.out.println("Dashing");
			pressed = true;
			return true;
		} else {
			index = 0;
			return false;
		}
	}
}
