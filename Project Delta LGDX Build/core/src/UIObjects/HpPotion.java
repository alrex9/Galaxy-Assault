package UIObjects;

import framework.NewGame;
import objects.UIObject;

public class HpPotion extends UIObject {

	public boolean leftPotion;

	public HpPotion(float x, float y, boolean clickable, boolean left, String... args) {
		super(x, y, clickable, args);
		this.leftPotion = left;
		if(leftPotion) {
			if(NewGame.playerProfile.getInteger("potions") == 1) {
				index = 1;
			} else {
				index = 0;
			}
		} else {
			if(NewGame.playerProfile.getInteger("potions") == 2) {
				index = 1;
			} else {
				index = 0;
			}
		}
	}
}
