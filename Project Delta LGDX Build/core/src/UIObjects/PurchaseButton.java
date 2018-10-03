package UIObjects;

import java.io.IOException;

import framework.NewGame;
import objects.UIObject;

public class PurchaseButton extends UIObject{

	public String type;


	public PurchaseButton(float x, float y, boolean clickable, int type, String... args) {
		super(x, y, clickable, args);
		if(type != 0) {
			//this.index = NewGame.userValues[type];
			if(type == 3) {
				this.type = "BossSword1";
			} else if(type == 4) {
				this.type = "BossSword2";
			} else if(type == 5) {
				this.type = "BossSword3";
			} else if(type == 6) {
				this.type = "BossSword4";
			} else {
				System.out.println("type is: " + type);
			}
		} else {
			this.type = "hp";
		}
	}

	public void pressed() {
		System.out.println(names[0] + " clicked");
		if(type.equals("hp")) {
			int hp = NewGame.playerProfile.getInteger("hp");
			if(this.names[0].equals("BuyHp")) {
				if(hp >= 6) {
					// add a cheer
					System.out.println("player already has max hp");
				} else {
					// run purchase code	
					super.pressed();
					NewGame.playerProfile.putInteger("hp", hp + 1);
				}
			} else {
				if(hp >= 3) {
					// add a cheer
					System.out.println("player already has max hp");
				} else {
					// run purchase code	
					super.pressed();
					NewGame.playerProfile.putInteger("hp", hp + 3);
				}
			}
		} else {
			if(NewGame.playerProfile.getInteger(type) == 1) {
				// add a cheer
				System.out.println("This bosssword already purchased.");
			} else {
				// run purchase
				super.pressed();
				NewGame.playerProfile.putInteger(type, 1);
			}
		}
		//		NewGame.m_platformResolver.requestPurchase(names[0]);
		//		System.out.println(names[index] + " was pressed");
		NewGame.playerProfile.flush();
	}

	public void update() {
		if(NewGame.playerProfile.getInteger(type) == 0) {
			index = NewGame.playerProfile.getInteger(type);
		} else {
			if(this.names[0].equals("BuyHp")) {
				if(NewGame.playerProfile.getInteger("hp") >= 6) {
					index = 1;
				} else {
					index = 0;
				}
			} else {
				if(NewGame.playerProfile.getInteger("hp") >= 4) {
					index = 1;
				} else {
					index = 0;
				}
			}
		}
	}

}
