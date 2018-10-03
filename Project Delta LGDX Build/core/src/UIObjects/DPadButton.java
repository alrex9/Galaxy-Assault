package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import framework.Assets;
import framework.NewGame;
import framework.PlayScreen;
import objects.UIObject;

public class DPadButton extends HUDObject {
	DPadCircle dPadCircle;

	public DPadButton(float x, float y, boolean clickable, DPadCircle dPadCircle, String... args) {
		super(x, y, clickable, args);
		this.dPadCircle = dPadCircle;
	}

	public void drawSprite(SpriteBatch batch) { 
		int temp = 0;
		if(dPadCircle.boundingRectangle.overlaps(this.boundingRectangle)) { 
//			System.out.println("touching button");
			temp = 1;	
		}
		batch.draw(Assets.findRegion(names[temp]), x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width, - height);
		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
