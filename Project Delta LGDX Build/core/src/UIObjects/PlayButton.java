package UIObjects;

import framework.Assets;
import framework.NewGame;
import framework.NewGame.STATE;
import objects.UIObject;

public class PlayButton extends UIObject {

	public PlayButton(float x, float y, boolean clickable, String... args) {
		super(x, y, clickable, args);
	}

	public void pressed() {
		NewGame.State = STATE.GAME;
		NewGame.menuScreen.hide();
		NewGame.menuScreen.paused = true;
		System.out.println(names[index] + " was pressed");
//		if(index < names.length - 1) {
//			index++;
//		}
		Assets.findSound("ClickNoise.wav").play(NewGame.gameVolume);
	}
}
