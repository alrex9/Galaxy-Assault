package UIObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import framework.Assets;
import framework.NewGame;
import framework.PlayScreen;
import objects.UIObject;

public class DPadCircle extends HUDObject {

	public final float RADIUS = 350;
	public float originX, originY;
	public boolean pressed;

	public DPadCircle(float x, float y, boolean clickable, String... args) {
		super(x, y, clickable, args);
		originX = x;
		originY = y;
		// TODO Auto-generated constructor stub
	}

	public void drawSprite(SpriteBatch batch) { // synch with player input
		boundingRectangle = new Rectangle(x, y, width, height);
		int temp = 0;
		if(y != originY && x != originX) {
			temp = 1;
		} 
		batch.draw(Assets.findRegion(names[temp]), x + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, y + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, width, height);
//				batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX() + PlayScreen.player.handler.gameCam.position.x - NewGame.V_WIDTH / 2, boundingRectangle.getY() + PlayScreen.player.handler.gameCam.position.y - NewGame.V_HEIGHT / 2, boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}

	public void pressed(int touchX, int touchY) {
		pressed = true;
		if(Math.abs(originX - touchX - (width / 2)) > RADIUS) {
			if(originX - touchX - (width / 2) > 0) {
				x = originX - touchX - (width / 2) + RADIUS;
			} else {
				x = originX - touchX + (width / 2) - RADIUS;
			}
		} else if(touchX == 0) { 
			pressed = false;
			x = originX;
		} else {
			x = touchX - width / 2;
		}

		//		System.out.println(Math.abs(touchY - originY - (height / 2)) + " " + RADIUS);
		if(Math.abs(touchY - originY - (height / 2)) > RADIUS / 2) {
			y = originY - RADIUS / 2 - (height / 2);
		} else if(touchY == 0) { 
			pressed = false;
			System.out.println("pressed = false");
			y = originY;
		} else {
			y = touchY - height / 2;
		}
	}
}
