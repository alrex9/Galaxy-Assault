package objects;

import java.io.IOException;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.ObjectId;
import framework.TwoDAnimation;

public class BossSword extends GameObject {

	public boolean swordOnscreen, explosionOnscreen;
	public Sprite sword, clouds, explosion;
	public TwoDAnimation cloudsAnim, explosionAnim;
	public float cloudsWidth, cloudsHeight, swordWidth, swordHeight, explosionWidth, explosionHeight;

	public BossSword(TextureRegion objectTex, float x, float y, Handler handler, ObjectId id) {
		super(Assets.findRegion("Jump"), x, y, handler, id);
		cloudsAnim = Assets.bossSwordClouds;
		cloudsAnim.speed = 0.057f;
		explosionAnim = Assets.swordExplosion;
		explosionAnim.speed = 0.057f;
		sword = new Sprite(Assets.findRegion("Sword"));
		clouds = new Sprite(cloudsAnim.currentImg);
		clouds.setRegion(cloudsAnim.images[0][0]);
		explosion = new Sprite(explosionAnim.currentImg);
		clouds.setRegionWidth(1000);
		clouds.setRegionHeight(400);
		swordWidth = 300;
		swordHeight = 777;
		explosion.setRegionWidth(1750);
		explosion.setRegionHeight(1700);
		clouds.setPosition(x, y);
		sword.setPosition(Boss.centerOfMass.x - swordWidth / 2, handler.gameCam.position.y - NewGame.V_HEIGHT / 2);
		explosion.setPosition(Boss.centerOfMass.x - explosion.getWidth() / 2, Boss.centerOfMass.y - explosion.getHeight() / 1.377f);
	}

	@Override
	public void tick(LinkedList<GameObject> object) {
		//		PlayScreen.renderList.add(this);
		if(clouds.getX() + clouds.getWidth() / 2 < Boss.centerOfMass.x) { 
			System.out.println("moving clouds in. current X vs target: " + clouds.getX() + ", " + Boss.centerOfMass);
			clouds.setRegion(cloudsAnim.currentImg);
			clouds.setX(clouds.getX() + NewGame.BASE_SPEED * 1.5777f * Gdx.graphics.getDeltaTime());
		} else if(cloudsAnim.totalCount != cloudsAnim.size / 2 && !swordOnscreen) {
			System.out.println("moving clouds in and running cloud animation. Animation count: " + cloudsAnim.totalCount);
			cloudsAnim.runAnimation();
			clouds.setRegion(cloudsAnim.currentImg);
		} else if(sword.getY() < Boss.centerOfMass.y - swordHeight / 1.2f) {
			System.out.println("dropping sword");
			swordOnscreen = true;
			sword.setY(sword.getY() + NewGame.BASE_SPEED * 2 * Gdx.graphics.getDeltaTime());
		} else if(explosionAnim.totalCount != explosionAnim.size + 1) {
			System.out.println("running explosion animation");
			Boss.autoKill = true;
			//			NewGame.State = STATE.GAME;
			explosionOnscreen = true;
			explosionAnim.runAnimation();
			explosion.setRegion(explosionAnim.currentImg);
		} else if(clouds.getX() < handler.gameCam.position.x + NewGame.V_WIDTH) {
			System.out.println("moving clouds out");
			explosionOnscreen = false;
			if(cloudsAnim.totalCount != cloudsAnim.size + 1) {
				cloudsAnim.runAnimation();
				clouds.setRegion(cloudsAnim.currentImg);
			}
			clouds.setX(clouds.getX() + NewGame.BASE_SPEED * 2 * Gdx.graphics.getDeltaTime());
		} else {
			System.out.println("removed boss sword object");
			cloudsAnim.reset();
			explosionAnim.reset();
			handler.disposeList.add(this);
			if(handler.bossCount == 1) {
				NewGame.playerProfile.putInteger("BossSword1", 0);
			} else if(handler.bossCount == 2) {
				NewGame.playerProfile.putInteger("BossSword2", 0);
			} else if(handler.bossCount == 3) {
				NewGame.playerProfile.putInteger("BossSword3", 0);
			} else if(handler.bossCount == 4) {
				NewGame.playerProfile.putInteger("BossSword4", 0);
			}
		}
	}

	public void draw(SpriteBatch batch) {
		clouds.setFlip(sprite.isFlipX(), true);
		sword.setFlip(sprite.isFlipX(), true);
		explosion.setFlip(sprite.isFlipX(), true);
		if(swordOnscreen) {
			batch.draw(sword, sword.getX(), sword.getY(), swordWidth, swordHeight);
		}
		if(explosionOnscreen) {
			explosion.draw(batch);
		}
		clouds.draw(batch);
	}
}
