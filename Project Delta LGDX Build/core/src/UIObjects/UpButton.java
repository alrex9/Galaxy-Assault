package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import framework.Assets;
import framework.NewGame;
import framework.PlayScreen;
import objects.UIObject;

public class UpButton extends HUDObject {
	
	DPadCircle dPadCircle;
	public boolean pressed;

	public UpButton(float x, float y, boolean clickable, DPadCircle dPadCircle, String... args) {
		super(x, y, clickable, args);
		this.dPadCircle = dPadCircle;
		width *= 3;
		x -= width / 3;
		boundingRectangle = new Rectangle(x, y, width, height);
		
	}

	public void drawSprite(SpriteBatch batch) { 
		int temp = 0;
		if(dPadCircle.boundingRectangle.overlaps(this.boundingRectangle)) { 
			pressed = true;
			temp = 1;	
		} else {
			pressed = false;
		}
		batch.draw(Assets.findRegion(names[temp]), x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width / 3, - height);
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX() + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, boundingRectangle.getY() + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
