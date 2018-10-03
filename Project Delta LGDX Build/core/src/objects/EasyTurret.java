package objects;

import framework.Handler;
import framework.ObjectId;

public class EasyTurret extends Turret{

	public EasyTurret(float x, float y, int difficulty, boolean upwards, Handler handler, ObjectId id) {
		super(x, y, difficulty, upwards, handler, id);
	}
}
