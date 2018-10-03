package UIObjects;

import java.io.IOException;

import framework.Assets;
import framework.MenuScreen;
import framework.NewGame;
import objects.UIObject;

public class VolumeAdjust extends UIObject{
	
	public boolean addative;
	
	public VolumeAdjust(float x, float y, boolean clickable, boolean addative, String... args) {
		super(x, y, clickable, args);
		this.addative = addative; 	
	}
	
	public void pressed() {
		super.pressed();
		System.out.println("Pressed");
		if(addative && NewGame.playerProfile.getInteger("volume") < 5) { 
			NewGame.playerProfile.putInteger("volume", NewGame.playerProfile.getInteger("volume") + 1);
			System.out.println("adding volume value once");
		} else if(!addative && NewGame.playerProfile.getInteger("volume") > 0){ 
			NewGame.playerProfile.putInteger("volume", NewGame.playerProfile.getInteger("volume") - 1);
			System.out.println("subtracting volume value once");
		}
		NewGame.playerProfile.flush();
	}
}
