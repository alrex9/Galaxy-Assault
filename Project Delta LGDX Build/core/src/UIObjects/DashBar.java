package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.Assets;
import framework.NewGame;
import framework.PlayScreen;
import objects.UIObject;

public class DashBar extends UIObject {

	public DashBar(float x, float y, boolean clickable, String... args) {
		super(x, y, clickable, args);
	}
	
	public void drawSprite(SpriteBatch batch) { 	
		if(PlayScreen.player.dashCD < PlayScreen.player.DASH_CD_TIME) {
			batch.draw(Assets.findRegion(names[0]), x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width, - height);	
			batch.draw(Assets.findRegion(names[2]), 17 + x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, -Assets.findRegion(names[2]).getRegionHeight() / 4 + y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, Assets.findRegion(names[2]).getRegionWidth() * (PlayScreen.player.dashCD / PlayScreen.player.DASH_CD_TIME) , - Assets.findRegion(names[2]).getRegionHeight());
		} else {
			batch.draw(Assets.findRegion(names[1]), x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width, - height);
		}
	}
	//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
}
