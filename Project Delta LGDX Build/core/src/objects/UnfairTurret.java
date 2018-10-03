package objects;

import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;

public class UnfairTurret extends Turret {

	public UnfairTurret(float x, float y, int difficulty, boolean upwards, Handler handler, ObjectId id) {
		super(x, y, difficulty, upwards, handler, id);
	}
	
	public void action2() { // default shooting action
		if(!fired) {
			handler.addList.add(new HomingMissle(Assets.findRegion("UpperTurret"), (float) (sprite.getX() + sprite.getWidth() / 2), (float) (sprite.getY() - sprite.getHeight() / 4), NewGame.V_WIDTH / 18, NewGame.V_HEIGHT / 20, ObjectId.EnemyProjectile, handler, theta));
		}
		fired = true;
		secondaryFire = false;
		movementState = 1;
		restTime = 3;
	}
}
