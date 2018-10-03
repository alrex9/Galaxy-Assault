package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.Assets;
import framework.NewGame;
import framework.PlayScreen;
import objects.UIObject;

public class HUDObject extends UIObject {

	public HUDObject(float x, float y, boolean clickable, String... args) {
		super(x, y, clickable, args);
		width = 100;
		height = 100;
		super.universalConstructor(x, y, clickable, args);
	}
	
	public void drawSprite(SpriteBatch batch) { 
		batch.draw(Assets.findRegion(names[index]), x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width, - height);
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
	
	public HUDObject(float x, float y, boolean clickable, boolean resize, String... args) {
		super(x, y, clickable, args);
	}
}
