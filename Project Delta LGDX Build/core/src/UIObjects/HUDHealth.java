package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.Assets;
import framework.NewGame;
import framework.PlayScreen;
import objects.UIObject;

public class HUDHealth extends UIObject {
	public int hpCount;
	
	public HUDHealth(float x, float y, boolean clickable, int hpCount, String... args) {
		super(x + 25 +  (hpCount * Assets.findRegion(args[0]).getRegionWidth() / 4), y, Assets.findRegion(args[0]).getRegionWidth() / 4 , Assets.findRegion(args[0]).getRegionHeight() / 4, clickable, args);
		this.hpCount = hpCount;
	}
	
	public void drawSprite(SpriteBatch batch) { 
		int temp = 0;
		if(PlayScreen.player.hp < hpCount) {
			temp = 1;
		} 
		batch.draw(Assets.findRegion(names[temp]), -100 + (25 * hpCount) + x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width, - height);
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
