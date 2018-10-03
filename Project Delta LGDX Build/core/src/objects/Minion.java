package objects;

import java.awt.Point;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import framework.ObjectId;
import framework.PlayScreen;
import framework.Assets;
import framework.Handler;
import framework.NewGame;

public class Minion extends Enemy { // walk towards player, perform action (potentially RNG the different jump lengths, speeds, rest times, etc or even have different specific type of spiderlings (passive, moderate, aggressive)
	
	private boolean jumped;
	private final int WALK_SPEED = 3;
	
	public Minion(TextureRegion spiderling_idle, float x, float y, Handler handler, ObjectId id) {
		super(Assets.findAnimation("BugFlying").images[0], x, y, handler, id);
		activeState = NewGame.STATE.BOSSFIGHT;
		hasGravity = true;
		gravity = 0.5f;
		velY = 10;
		jumped = false;
	}
	
	protected void setVariables() {
//		System.out.println(framework.PlayScreen.player.sprite.getOriginY());
	}
	
	public void tick(LinkedList<GameObject> object) { // eventually to be removed
    	System.out.println("movement state: " + movementState + ", Xpos: " + sprite.getX() + ", velX: " + velX + ", Ypos: " + sprite.getY() + ", velY: " + velY + ", rest time: " + restTime + ", rest count: " + restCount + ", next movement state " + nextMovementState + ", targetPoint " + targetPoint.toString());
    	movements(object);
        attack(object);
    }
	
// old action0
//    public void action0() { 
//		if(sprite.getY() == PlayScreen.player.sprite.getOriginY()) {
//			velY = 0;
//			velX = 0;
//		} 
//	}
    
	public void action2() { // go to a location
		System.out.println("triggered");
		if(PlayScreen.player.sprite.getOriginY() == sprite.getY()) {
			targetPoint = new Point((int) PlayScreen.player.sprite.getX(), (int) PlayScreen.player.sprite.getOriginY());
			System.out.println(targetPoint.toString());
		}
		velX = WALK_SPEED;
		if(Math.abs(sprite.getX() - PlayScreen.player.sprite.getX()) < 125 && sprite.getY() == PlayScreen.player.sprite.getOriginY()) {
			System.out.println("initiating jump");
			velY = -10; // JUMP_CONSTANT;
			velX = (float) (WALK_SPEED * 1.5);
			jumped = true;
		}
		if(moveTo(targetPoint, Math.abs(velX), Math.abs(velY)) || (jumped && sprite.getY() == PlayScreen.player.sprite.getOriginY())) {
			System.out.println("initiating rest");
			jumped = false;
			restTime = r.nextInt((int) restTime) + restTime;
			reset();
		} 
	}

// old spiderling action1
//	public void action1() { // go to a location
//		if(sprite.getOriginY() == sprite.getY()) {
//			targetPoint = new Point((int) (PlayScreen.player.sprite.getX() + ((PlayScreen.player.sprite.getX()  - sprite.getX()) * 2)), 0);
//			// System.out.println(targetPoint.toString());
//		}
//		xSpeed = NewGame.BASE_SPEED;  	
//		//System.out.println("movement state: " + movementState + ", Xpos: " + x + ", velX: " + velX + ", Ypos: " + y + ", velY: " + velY + ", rest time: " + restTime + ", rest count: " + restCount + ", next movement state " + nextMovementState);
////		System.out.println((Math.abs((sprite.getX() - PlayScreen.player.sprite.getX())) < 125) + " PlayScreen.player.sprite.getX() = " + PlayScreen.player.sprite.getX() + ", thisX = " + sprite.getX());
////		System.out.println((sprite.getY() + gravity >= PlayScreen.player.sprite.getOriginY() || sprite.getY() - gravity <= PlayScreen.player.sprite.getOriginY()));
////		System.out.println(jumped);
//		if(sprite.getX() - PlayScreen.player.sprite.getX() < 125 && (sprite.getY() + gravity >= sprite.getY() || sprite.getY() - gravity <= sprite.getY()) && !jumped) {
//			System.out.println("initiating jump");
//			velY = -NewGame.BASE_SPEED * handler.deltaTime; // JUMP_CONSTANT;
////			velX = (float) (NewGame.BASE_SPEED * 1.5);
//			jumped = true;
//			System.out.println("setting y vel");
//		}
//		if(moveTo(targetPoint, Math.abs(xSpeed), Math.abs(0)) ) {
//			System.out.println("initiating rest");
//			jumped = false;
//			restTime = (float) (r.nextDouble() + restTime);
//			reset();
//		}
//	}
}
