package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;

public class background {

	public final int MOVE_CONSTANT = 3;
	public float x, y, width, height;
	public boolean flipX, flipY;
	public Handler handler;

	public background(float x, float y, Handler handler, ObjectId id, boolean flipX, boolean flipY) {
		this.x = x;
		this.y = y;
		width = handler.background.getRegionWidth();
		height = handler.background.getRegionHeight();
		this.flipX = flipX;
		this.flipY = flipY;
		this.handler = handler;
	}

	public void tick() {
		if(x + width < handler.gameCam.position.x - NewGame.V_WIDTH / 2) {
			x += width * 2;
		}
		if(y > handler.gameCam.position.y + NewGame.V_HEIGHT / 2) {
			y -= height * 2;
		}
	}

	public void draw(SpriteBatch batch) {
		handler.background.setFlip(flipX, flipY);
		batch.draw(handler.background, x, y);
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
