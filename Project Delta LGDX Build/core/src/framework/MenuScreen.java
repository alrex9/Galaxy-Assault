package framework;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import UIObjects.HpPotion;
import UIObjects.PlayButton;
import UIObjects.PurchaseButton;
import UIObjects.VolumeAdjust;
import UIObjects.VolumeTick;
import objects.UIObject;

public class MenuScreen extends ApplicationAdapter implements Screen, InputProcessor  {

	private Viewport gamePort;
	public SpriteBatch batch;
	public OrthographicCamera gameCam;
	public ArrayList<UIObject> elements;
	public static ArrayList<Sound> playSounds;
	public Music mainSong;
	public boolean paused, init;

	public MenuScreen(NewGame game) throws FileNotFoundException {
		System.out.println("new menu created");
		NewGame.playScreenLoaded = false;
		batch = new SpriteBatch();
		gameCam = new OrthographicCamera(); 
		gameCam.setToOrtho(true, NewGame.V_WIDTH, NewGame.V_HEIGHT);
		gamePort = new StretchViewport(NewGame.V_WIDTH, NewGame.V_HEIGHT, gameCam);
		gamePort.apply();
		elements = new ArrayList<UIObject>();
		playSounds = new ArrayList<Sound>();
		paused = false;
		System.gc();
	}

	public void resize(int width, int height) { gamePort.update(width, height);}

	public void show() {
	}

	public void init() { 
//		elements.add(new UIObject(0, 0, false, "MenuNebula"));
		elements.add(new HpPotion(25, -25, false, true, "EmptyPotion", "ReducedPotion"));
		elements.add(new HpPotion(50 + Assets.findRegion("EmptyPotion").getRegionWidth(), -25, false, false, "EmptyPotion", "ReducedPotion"));
		elements.add(new UIObject(700, 75, false, "equals1Hp"));
		elements.add(new PlayButton(50, 350, true, "Play", "PlayPressed"));
		elements.add(new UIObject(50, 750, true, "Credits", "CreditsPressed"));	
		elements.add(new PurchaseButton(1350, 5, true, 0, "BuyHp", "BuyHpPressed"));
		elements.add(new PurchaseButton(1950, 5, true, 0, "Buy3Hp", "Buy3HpPressed"));
		elements.add(new PurchaseButton(1350, 490, true, 3, "BuySword1", "BuySword1Pressed"));
		elements.add(new PurchaseButton(1950, 490, true, 4, "BuySword2", "BuySword2Pressed"));
		elements.add(new PurchaseButton(1350, 965, true, 5, "BuySword3", "BuySword3Pressed"));
		elements.add(new PurchaseButton(1950, 965, true, 6, "BuySword4", "BuySword4Pressed"));
		elements.add(new VolumeAdjust(50, 1150, true, false, "MinusSound"));
		elements.add(new UIObject(250, 1175, false, "SoundIcon"));
		elements.add(new VolumeTick(425, 1200, false, 1, "SoundBar"));
		elements.add(new VolumeTick(500, 1200, false, 2, "SoundBar"));	
		elements.add(new VolumeTick(575, 1200, false, 3, "SoundBar"));
		elements.add(new VolumeTick(650, 1200, false, 4, "SoundBar"));
		elements.add(new VolumeTick(725, 1200, false, 5, "SoundBar"));
		elements.add(new VolumeAdjust(825, 1175, true, true, "PlusSound"));
		//mainSong = Assets.findTrack("RegularMusic(PurplePlanet).mp3");
		//mainSong.setLooping(true);
	}
	public void pause() {}
	public void resume() {}
	public void hide() {}
	public void dispose() {batch.dispose();}

	public void render(float delta) {
		if(!paused) {
			if(!init) {
				init = true;
				init();
			}
			NewGame.playScreenLoaded = false;
			//			Assets.tracks.get("BossMusic(PurplePlanet).mp3").setVolume(NewGame.volumeMultiplyer * .2f);
			//			Assets.tracks.get("BossMusic(PurplePlanet).mp3").play();
			Gdx.input.setInputProcessor(this);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			Gdx.gl.glClearColor(30/255f, 32/255f, 43/255f, 1);
			batch.begin();
			batch.setProjectionMatrix(gameCam.combined);
			for(UIObject e : elements) {
				if(e.getClass() == PurchaseButton.class) {
					((PurchaseButton) e).update();
				}
				e.drawSprite(batch);
			}
			batch.end();
			for(int i = 0; i < playSounds.size(); i++) {
				playSounds.get(i).play(NewGame.volumeMultiplyer * .2f);
				playSounds.remove(i);
			}
		}
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		System.out.println(screenX * NewGame.V_WIDTH / Gdx.graphics.getWidth() + " " + screenY * NewGame.V_WIDTH / Gdx.graphics.getWidth());
		System.out.println("NewGame.V_WIDTH Value: " + NewGame.V_WIDTH + " , graphics api width" + Gdx.graphics.getWidth());
		System.out.println("NewGame.V_HEIGHT Value: " + NewGame.V_HEIGHT + " , graphics api height" + Gdx.graphics.getHeight());
		if(!paused) {
			screenX *= NewGame.V_WIDTH / Gdx.graphics.getWidth();
			screenY *= NewGame.V_HEIGHT / Gdx.graphics.getHeight();
			Vector2 temp = new Vector2(screenX, screenY);
			elements.add(new UIObject(screenX, screenY, true, "Credits", "CreditsPressed"));
			for(UIObject e : elements) {
				if(e.clickable && e.checkInput(temp)) {
					e.pressed();
					return true;
				}
			}
		}	
		return false;
	}

	public boolean keyDown(int keycode) {return false;}
	public boolean keyUp(int keycode) {return false;}
	public boolean keyTyped(char character) {return false;}
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}
	public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
	public boolean mouseMoved(int screenX, int screenY) {return false;}
	public boolean scrolled(int amount) {return false;}
}
