package objects;

import framework.Handler;
import framework.NewGame;
import framework.ObjectId;

public class HardTurret extends Turret{

	public HardTurret(float x, float y, int difficulty, boolean upwards, Handler handler, ObjectId id) {
		super(x, y, difficulty, upwards, handler, id);
	}
}
