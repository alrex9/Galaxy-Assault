package UI;

import java.util.ArrayList;

import UIObjects.AttackButton;
import UIObjects.DPadCircle;
import UIObjects.DashBar;
import UIObjects.DashButton;
import UIObjects.HUDHealth;
import UIObjects.HUDObject;
import UIObjects.SideButton;
import UIObjects.UpButton;
import objects.UIObject;

public class HUD {
	public ArrayList<UIObject> elements;


	public HUD() {
		elements = new ArrayList<UIObject>();
		elements.add(new HUDObject(-2, -2, false, false, "HUDBackground"));
		elements.add(new DashBar(25, 127, false, "HUDDashBar", "HUDDashBarFull", "HUDDashBarOnlypsd"));
		elements.add(new HUDHealth(0, 10, false, 1, "FullHealth","NoHealth"));
		elements.add(new HUDHealth(0, 10, false, 2, "FullHealth","NoHealth"));
		elements.add(new HUDHealth(0, 10, false, 3, "FullHealth","NoHealth"));
		elements.add(new HUDHealth(0, 10, false, 4, "FullHealth","NoHealth"));
		elements.add(new HUDHealth(0, 10, false, 5, "FullHealth","NoHealth"));
		elements.add(new HUDHealth(0, 10, false, 6, "FullHealth","NoHealth"));
		DPadCircle tempCircle = new DPadCircle(175,1300, true, "Middle", "DarkMiddle");
		elements.add(new SideButton(50,1300, true, tempCircle, true, "LeftArrow", "DarkLeftArrow"));
		elements.add(new SideButton(300,1300, true, tempCircle, false, "RightArrow", "DarkRightArrow"));
		elements.add(new UpButton(175,1175, true, tempCircle, "Jump", "DarkJump"));
		elements.add(tempCircle);
		elements.add(new DashButton(2250,1280, true, "Phase", "DarkPhase"));
		elements.add(new AttackButton(2400,1170, true, "Attack", "DarkAttack"));
	} 
}
