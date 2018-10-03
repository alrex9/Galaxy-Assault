package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.Sprite;

import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;
import framework.PlayScreen;

public class Potion extends GameObject {
	
	public Sprite sprite;

	public Potion(float x, float y, Handler handler, ObjectId id) {
		super(x, y, handler, id);
		sprite = new Sprite(Assets.findRegion("Potion"));
		sprite.setPosition(x, y);
		sprite.setOrigin(x, y);
		if (NewGame.playerProfile.getInteger("hp") != 6 && (int) (Math.random() * 100 * Math.random() * 100) != 0) {
			handler.disposeList.add(this);
		}
	}

	@Override
	public void tick(LinkedList<GameObject> object) {
		if (this.boundingRectangle.overlaps(PlayScreen.player.boundingRectangle)) {
			int temp = NewGame.playerProfile.getInteger("potions") + 1;
			NewGame.playerProfile.putInteger("potions", temp);
			if(temp == 2) {
				NewGame.playerProfile.putInteger("potions", 0);
				if(NewGame.playerProfile.getInteger("hp") < 6) {
					NewGame.playerProfile.putInteger("hp", NewGame.playerProfile.getInteger("hp") + 1);
				}
			}
			NewGame.playerProfile.flush();
		}
	}
}
