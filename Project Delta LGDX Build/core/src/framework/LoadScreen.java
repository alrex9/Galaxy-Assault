package framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.FileHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LoadScreen implements Screen {

	public OrthographicCamera camera;
	public SpriteBatch batch;
	public Sprite LdScreen, spinningBall;// backGround,
	//	public TwoDAnimation companyAnim;
	public NewGame game;
	private float rotation;
	private boolean playerFileLoaded;

	public LoadScreen(NewGame newGame) {
		game = newGame;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 2560, 1440);
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		Assets.preLoad();
		System.out.println("Loading load screen assests");
//		companyAnim = new TwoDAnimation(new TextureRegion(new Texture(Gdx.files.internal("Isigdri.png"))).split(2809, 1004));
		System.out.println("initialized splash screen");
//		companyAnim.speed = .000000000000000000000000000000000000000000001f;
		Texture LdscreenTexture = new Texture(Gdx.files.internal("SplashScreen.png"));
		System.out.println("loaded splash screen");
	//Texture bckGrnd = new Texture(Gdx.files.internal("MenuNebula.png"));
		System.out.println("loaded nebula");
		Texture ball = new Texture(Gdx.files.internal("LoadBall.png"));
		System.out.println("loaded ball");
		LdScreen =  new Sprite (LdscreenTexture);
//		backGround = new Sprite(bckGrnd);
		spinningBall = new Sprite(ball);
		System.out.println("initialized load screen objects");
		spinningBall.setOrigin(0, spinningBall.getHeight() / 2);
		System.out.println("Completed loading load screen assets");
		Assets.manager.finishLoading();
		rotation = 0.005f;
//		companyAnim.completeOnce = true;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		Gdx.gl.glClearColor(30/255f, 32/255f, 43/255f, 1);
//		if(companyAnim.completeOnce && rotation < 0) {
//			batch.draw(backGround, 0, 0);
		batch.draw(LdScreen, 0,0);
		rotation -= 180 * Gdx.graphics.getDeltaTime();
		batch.draw(spinningBall, 1150, 0, spinningBall.getOriginY(), spinningBall.getOriginY(), spinningBall.getWidth(), spinningBall.getHeight(), 1, 1, rotation);
//		} else {
//			if(companyAnim.completeOnce && rotation > 0) {
//				rotation -= Gdx.graphics.getDeltaTime();
//			} else {
//				companyAnim.runAnimation();
//			}
//			batch.draw(companyAnim.currentImg, -150, 150);
//		}
		batch.end();
		Assets.load();
		Assets.manager.update();
		System.out.println(Assets.manager.getProgress() * 100 + "%");
		if(Assets.manager.getProgress() == 1) {
			Assets.postLoad();
			game.setScreen(NewGame.menuScreen);
			LdScreen.getTexture().dispose();
//			backGround.getTexture().dispose();
			spinningBall.getTexture().dispose();
		}
		//
		//		Assets.manager.update();
		//		game.setScreen(NewGame.menuScreen);
		//		System.out.println(Assets.manager.getProgress() * 100 + "%");
		//				Assets.manager.finishLoading();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
}
