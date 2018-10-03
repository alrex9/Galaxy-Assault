package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;
import framework.Assets;
import framework.Handler;
import framework.ObjectId;
import framework.NewGame;

public class Block extends GameObject implements Poolable {
	
	public boolean flipX;
	public Sprite sprite;

	public Block(float x, float y, int type, Handler handler, ObjectId id) {
		super(x, y, handler, id);
		sprite = new Sprite(Assets.findRegion("MiddlePink"));
		sprite.setPosition(x, y);
		sprite.setOrigin(x, y);
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth() * scaleValue, sprite.getHeight() * scaleValue);
	}

	public void tick(LinkedList<GameObject> object) {
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth() * scaleValue, sprite.getHeight() * scaleValue);
		if(sprite.getX() < handler.gameCam.position.x - NewGame.V_WIDTH / 2 - sprite.getWidth()) {
			handler.disposeList.add(this);
		}
	}

	public void draw(SpriteBatch batch) {
		sprite.setFlip(flipX, true);
//		if(sprite.getX() < handler.gameCam.position.x + NewGame.V_WIDTH / 2 + sprite.getWidth() && sprite.getY() > handler.gameCam.position.y - NewGame.V_HEIGHT / 2 - sprite.getHeight()) {
			drawSprite(batch);
//		}
	}
	public void drawSprite(SpriteBatch batch) { 
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getWidth() * scaleValue, sprite.getHeight() * scaleValue);
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth() * scaleValue, sprite.getHeight() * scaleValue);
	}

	@Override
	public void reset() {
		// resetting old block. Add paramter that changes the skin
	}

	public void drawSprite1(SpriteBatch batch) { 
		batch.draw(sprite, x, y, width, height);
		boundingRectangle = new Rectangle(x, y, width, height); // Swapped out old calculations that multiplied original image size by the width and height
	}
}	


