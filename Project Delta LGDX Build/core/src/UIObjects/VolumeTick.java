package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.NewGame;
import objects.UIObject;

public class VolumeTick extends UIObject {
	
	public int index;

	public VolumeTick(float x, float y, boolean clickable, int index, String... args) {
		super(x, y, clickable, args);
		this.index = index;
	}
	
	public void drawSprite(SpriteBatch batch) { 
		if(NewGame.playerProfile.getInteger("volume") >= index) {
			super.drawSprite(batch);
		} 
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
