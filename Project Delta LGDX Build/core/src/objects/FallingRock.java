package objects;

import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;

public class FallingRock extends EnemyProjectile {
	
	protected float gravity;

	public FallingRock(TextureRegion tex, float x, float y, int width, int height, ObjectId id, Handler handler, double theta) {
		super(tex, x, y, id, handler, theta);
		Random r = new Random();
		velY = (float) (-NewGame.BASE_SPEED * (r.nextDouble() + 1));
		setScale(.25f);
		sprite.setOriginCenter();
		sprite.setRotation(r.nextInt(360));
	}
	
	public void tick(LinkedList<GameObject> object) {
		
//		System.out.println("Rock position: " + sprite.getX() + " , " + sprite.getY() + " player position: " + PlayScreen.player.sprite.getX() + " , " + PlayScreen.player.sprite.getY());
		sprite.setY(sprite.getY() + velY * gravity * handler.deltaTime);
		gravity -= 0.1;
		if (sprite.getY() > handler.gameCam.position.y + NewGame.V_HEIGHT / 2) {
			handler.disposeList.add(this);
		}
	}
	
	public void drawSprite(SpriteBatch batch) { 
//		sprite.setOriginCenter();
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(), width, height, 1f, 1f, sprite.getRotation());
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY(), width, height); // Swapped out old calculations that multiplied original image size by the width and height
	}

}
