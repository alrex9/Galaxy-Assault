package objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import framework.Handler;
import framework.NewGame;
import framework.ObjectId;
import framework.PlayScreen;

public class HomingMissle extends EnemyProjectile {
	private static final float SPEED = NewGame.BASE_SPEED;
	protected double angularAcceleration, directionalAcceleration, additionalOmega, additionalAccel, actualTheta;
	protected double maxThetaChange = 120;

	public HomingMissle(TextureRegion tex, float x, float y, int width, int height, ObjectId id, Handler handler, double theta) { // 
		super(tex, x, y, id, handler, theta);
		angularAcceleration = .01;
//		System.out.println("created homing missle");
		actualTheta = theta - 720;
		
	}

	public void tick(LinkedList<GameObject> object) {
//		System.out.println("Xpos: " + sprite.getX() + ", velX: " + velX + ", Ypos: " + sprite.getY() + ", velY: " + velY +  ", ");
		checkOffScreen(object);
		if(PlayScreen.player.sprite.getBoundingRectangle().overlaps(new Rectangle((int) sprite.getX(),(int) sprite.getY(), sprite.getWidth(), sprite.getHeight()))) {
			System.out.println("removing homing missle");
			handler.disposeList.add(this);
		}
		actualTheta = PointTowards(PlayScreen.player);
		sprite.setOriginCenter();
		sprite.setRotation((float) actualTheta);
		velX = (float) (((SPEED) * handler.deltaTime) * Math.cos(Math.toRadians(actualTheta)));
		velY = (float) (((SPEED) * handler.deltaTime) * Math.sin(Math.toRadians(actualTheta)));
		move();
		// additionalOmega += angularAcceleration;
	}

	public double PointTowards(Player player) { 
		return theta = (float) (Math.toDegrees((Math.atan2((sprite.getY() + sprite.getHeight() / 2) - (player.sprite.getY() + player.sprite.getHeight() / 2), (sprite.getX() + sprite.getWidth() / 2) - (player.sprite.getX() + player.sprite.getWidth() / 2)))) + 180 - 720);
//		System.out.println("theta " + theta + ", delta theta " + (theta - actualTheta) + " , actual theta: " + actualTheta);
//		if(theta > actualTheta) {
//			System.out.println("adding");
//			return actualTheta + maxThetaChange * handler.deltaTime;
//		} else {
//			System.out.println("subtracting");
//			return actualTheta - maxThetaChange * handler.deltaTime;
//		}
		
//		if(Math.abs(theta - tempTheta) * handler.deltaTime > maxThetaChange) {
//			System.out.println("returning the direct angle to the player");
//			return tempTheta;
//		} else if(tempTheta - theta < 0) {
//			System.out.println("@@@@@@@@@@@@@");
//			return theta - maxThetaChange * handler.deltaTime * (Math.abs(theta - tempTheta) / (theta - tempTheta));
//		} else {
//			return theta + maxThetaChange * handler.deltaTime * (Math.abs(theta - tempTheta) / (theta - tempTheta));
//		}
//	}
	}	
}
