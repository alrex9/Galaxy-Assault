package objects;

import java.util.LinkedList;

import com.badlogic.gdx.math.Rectangle;

import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.NewGame.STATE;
import framework.ObjectId;
import framework.PlayScreen;

public class BossHandler extends GameObject {

	public BossHandler(float x, float y, Handler handler, ObjectId id) {
		super(Assets.findRegion("MiddleBlue"), x, y, handler, id);
	}

	@Override
	public void tick(LinkedList<GameObject> object) {
		if(PlayScreen.player.sprite.getX() >= sprite.getX()) {
			object.remove(this);
			for(int i = 0; i < object.size(); i++) {
				GameObject tempObject = object.get(i);
				if(tempObject.id == id) {
					handler.removeObject(tempObject);
				}
			}
			NewGame.State = STATE.TRANSITIONTOBOSS;
		} else if(NewGame.State == STATE.TRANSITIONTOBOSS) {
			object.remove(this);
		}
	}

	public Rectangle getBoundsBottom() {return null;}
	public Rectangle getBoundsTop() {return null;}
	public Rectangle getBoundsLeft() {return null;}
	public Rectangle getBoundsRight() {return null;}
}
