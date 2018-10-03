package framework;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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

import UI.HUD;
import UIObjects.BossSwordButton;
import UIObjects.DPadCircle;
import framework.NewGame.STATE;
import objects.BossSword;
import objects.GameObject;
import objects.Player;
import objects.background;

public class PlayScreen implements Screen, InputProcessor{
	public OrthographicCamera gameCam;
	public static HUD hud;
	private Viewport gamePort;
	public Handler handler;
	public static SpriteBatch batch;
	public static Player player;
	private static final float CAM_TRAVEL_TIME = 1f; // in seconds
	private float camTravelTime = CAM_TRAVEL_TIME;
	public float camDeltaX, camDeltaY;
	public boolean camDeltasSet;
	public float timer;
	public int frameCount;
	public BufferedImageLoader loader;
	public static ArrayList<GameObject> renderList;
	public static ArrayList<Sound> playSounds;
	public Music mainSong;

	public PlayScreen(NewGame game) throws FileNotFoundException {
		System.out.println("init playscreen");
		batch = new SpriteBatch();
		gameCam = new OrthographicCamera(); 
		gameCam.setToOrtho(true, NewGame.V_WIDTH, NewGame.V_HEIGHT);
		gamePort = new StretchViewport(NewGame.V_WIDTH, NewGame.V_HEIGHT, gameCam);
		gamePort.apply();
		System.out.println("about to init handle");
		handler = new Handler(gameCam);
		System.out.println("init handler");
		loader = new BufferedImageLoader();
		handler.loadImageLevel(loader.loadImage("/level1.png"));
		System.out.println("loaded level1");
		gameCam.zoom = 1f;
		camDeltasSet = false;
		gameCam.position.x = player.sprite.getX() + NewGame.V_WIDTH / 3;
		gameCam.position.y = player.sprite.getY() + NewGame.V_HEIGHT + 3;
		renderList = new ArrayList<GameObject>();
		playSounds = new ArrayList<Sound>();
		hud = new HUD();
		Gdx.input.setInputProcessor(this);
		System.out.println("playscreen init complete");
//		mainSong = Assets.findTrack("RegularMusic(PurplePlanet).mp3");
//		mainSong.setLooping(true);
	}

	public void update(float dt) {
		frameCount++;
		timer += dt;
		if(timer > 1) {
			//			System.out.println("Frames per second: " + frameCount);
			frameCount = 0;
			timer = 0;
		}
		handleInput(dt);
		handler.tick(dt);
		player.tick(handler.object);
		tickCamera(dt);
		gameCam.update();
		renderList.clear();
		for(int i = 0; i < handler.object.size(); i++) {
			if((handler.object.get(i).sprite.getX() < handler.gameCam.position.x + NewGame.V_WIDTH / 2 + handler.object.get(i).width
					&& handler.object.get(i).sprite.getY() > handler.gameCam.position.y - NewGame.V_HEIGHT / 2)) {
				renderList.add(handler.object.get(i));
			} else if (handler.object.get(i).getClass() == BossSword.class) {
				renderList.add(handler.object.get(i));
			}
		}
		//		System.out.println(renderList.size() + ", " + handler.object.size() + ", " + handler.blockCount);
	}

	public void tickCamera(float dt) {
		float tempX = gameCam.position.x;
		float tempY = gameCam.position.y; 
		if(player.sprite.getY() - PlayScreen.player.height > gameCam.position.y + NewGame.V_HEIGHT / 2) { // handles player death
			player.hp = 0;
			//player.deathAnimation(); or something  
			//				System.out.println("Out of bounds");
			//			} else if(player.getVelY() < 0 && player.sprite.getY() <  gameCam.position.y + (NewGame.V_HEIGHT / 1.1) && gameCam.position.y < gameCam.position.y + (-player.sprite.getY() + NewGame.V_HEIGHT / 5 - (gameCam.position.y)) * 0.05) { // Makes camera go up and only up with players movement 
			//				gameCam.position.y += (-player.sprite.getY() + NewGame.V_HEIGHT / 3 - (gameCam.position.y)) * 0.05;
		}
		if(NewGame.State == STATE.GAME) { 
			gameCam.position.x += NewGame.BASE_SPEED / 3 * dt;
			// gameCam.position.y = player.sprite.getY(); //remove later
			if(player.sprite.getX() <= gameCam.position.x - NewGame.V_WIDTH / 2) { // makes sure the player doesn't go left if the screen
				player.sprite.setX(gameCam.position.x - NewGame.V_WIDTH / 2); 
			} else if(player.sprite.getX() > gameCam.position.x + NewGame.V_WIDTH / 7) { // Makes camera follow the player to the right 
				gameCam.position.x = player.sprite.getX() - NewGame.V_WIDTH / 7;
			} 
			if(gameCam.position.y > player.sprite.getY() && player.getVelY() < 0) {
				gameCam.position.y += player.getVelY();
			} else if (gameCam.position.y > player.sprite.getY() - NewGame.V_HEIGHT / 27) {
				gameCam.position.y = player.sprite.getY() - NewGame.V_HEIGHT / 27;
			}
		} else if(NewGame.State == STATE.BOSSFIGHT) {
			if(player.sprite.getX() <= gameCam.position.x - NewGame.V_WIDTH / 2) { // makes sure the player doesn't go left if the screen
				player.sprite.setX(gameCam.position.x - NewGame.V_WIDTH / 2); 
			} else if(player.sprite.getX() >= gameCam.position.x + NewGame.V_WIDTH / 2 - player.width) { // makes sure the player doesn't go left if the screen
				player.sprite.setX(gameCam.position.x + NewGame.V_WIDTH / 2 - player.width); 
			}
		} else if(NewGame.State == STATE.TRANSITIONTOBOSS) {
			player.setVelX(0);
			if(!camDeltasSet) {
				setCamDeltas();
			}
			if(gameCam.position.x != handler.camCenterX || gameCam.position.y != handler.camCenterY) {
				cameraGoTo(dt);
			}
		} else if(NewGame.State == STATE.TRANSITIONFROMBOSS) {
			player.setVelX(0);
			if(!camDeltasSet) {
				camDeltaX = (player.sprite.getX() + NewGame.V_WIDTH / 2 - handler.gameCam.position.x);
				camDeltaY = handler.camCenterY - gameCam.position.y;
				camDeltasSet = true;
			}
			if(camTravelTime <= 0) {
				camDeltasSet = false;
				NewGame.State = STATE.GAME;
				camTravelTime = CAM_TRAVEL_TIME;
			} else {
				cameraGoTo(dt);
				camTravelTime -= dt;
			}
		} else if (NewGame.State == STATE.BOSSSWORD) {
			player.setVelX(0);
		}
		float deltaX = gameCam.position.x - tempX;
		float deltaY = gameCam.position.y - tempY;
		for(background g : handler.backgrounds) {
			g.setPosition(g.x + (deltaX * 0.3777f), g.y + (deltaY) - (deltaY / 1.777f));
		}
	}

	public void cameraGoTo (float dt) {
		if(camTravelTime <= 0) {
			gameCam.position.x = handler.camCenterX;
			gameCam.position.y = handler.camCenterY;
			NewGame.State = STATE.BOSSFIGHT;
			int bossCount = (int) (handler.bossCount + 1);
			String name = "";
			if(bossCount == 3) {
				name = "BossSword1";
			} else if(bossCount == 4) {
				name = "BossSword2";
			} else if(bossCount == 5) {
				name = "BossSword3";
			} else if(bossCount == 6) {
				name = "BossSword4";
			}
			if(NewGame.playerProfile.getInteger(name) == 1) { 
				PlayScreen.hud.elements.add(new BossSwordButton(1200, 25, true, "BuySword1", "BuySword1Pressed"));
			}
			camDeltasSet = false;
			camTravelTime = CAM_TRAVEL_TIME;
		} 
		else{
			float travelX = camDeltaX * dt / CAM_TRAVEL_TIME;
			float travelY = camDeltaY * dt / CAM_TRAVEL_TIME;
			gameCam.position.x += travelX; // probably change
			gameCam.position.y += travelY;
			camTravelTime -= dt;
		}
	}

	public void handleInput(float dt) { 
		float moveSpeed = NewGame.BASE_SPEED;
		float jumpSpeed = NewGame.BASE_SPEED * 2;
		boolean circlePressed = false;
		if(Gdx.input.isTouched() && player.hp > 0) { // used for user input on touchscreen devices
			for(int i = 0; i < 2; i++) {
				int touchX = Gdx.input.getX(i) * NewGame.V_WIDTH / Gdx.graphics.getWidth();
				int touchY = Gdx.input.getY(i) * NewGame.V_HEIGHT /  Gdx.graphics.getHeight();

				for(int j = 0; j < hud.elements.size(); j++) {	
					if(hud.elements.get(j).clickable && hud.elements.get(j).checkInput(new Vector2(touchX,touchY))) {
						if(hud.elements.get(j).getClass() == DPadCircle.class) {
							((DPadCircle) hud.elements.get(j)).pressed(touchX, touchY);
							circlePressed = true;
						} else {
							hud.elements.get(j).pressed();
						}
					}
				} 
			}
		}
		//		for(int j = 0; j < hud.elements.size(); j++) {
		//			if(NewGame.State == STATE.GAME || NewGame.State == STATE.BOSSFIGHT) {
		//				if(!player.dashing && player.attackState == 0) { 
		//					if(hud.elements.get(j).getClass() == UpButton.class && ((UpButton) (hud.elements.get(j))).pressed) {
		//						jumpSpeed = (float) (moveSpeed * 1.5); // change
		//						if(player.getJumping() < 2 && player.getVelY() > - jumpSpeed / (jumpSpeed - 1)) {

		//							if(player.getJumping() == 0) {
		//								player.playerJump.setCount(0); 
		//								player.playerDoubleJump.setCount(0);
		//								player.setVelY(- jumpSpeed);
		//								player.setJumping(player.getJumping() + 1); // fix this
		//							} else {
		//								//							player.playerJump.setCount(0);
		//								player.setVelY((float) (- jumpSpeed / 1.2));
		//								player.setJumping(player.getJumping() + 1); // fix this
		//							}
		//						}
		//
		//					}
		//					if(hud.elements.get(j).getClass() == SideButton.class) {
		//						if(((SideButton) (hud.elements.get(j))).left) {
		//							if(((SideButton) (hud.elements.get(j))).pressed) { // equivelent of pressing a
		//								player.setVelX(-moveSpeed);
		//								player.sprite.setFlip(true, player.sprite.isFlipY());
		//							} else if(player.getVelX() < 0) {
		//								player.playerWalk.setCount(0);
		//								player.setVelX(0);
		//							} 
		//						}
		//						if(!((SideButton) (hud.elements.get(j))).left) {
		//							if(((SideButton) (hud.elements.get(j))).pressed) { // equivelent of pressing d
		//								player.setVelX(moveSpeed);
		//								player.sprite.setFlip(false, player.sprite.isFlipY());
		//							} else if(player.getVelX() > 0) {
		//								player.playerWalk.setCount(0);
		//								player.setVelX(0);
		//							} 
		//						}
		//					}
		//					if(hud.elements.get(j).getClass() == DashButton.class && ((DashButton) (hud.elements.get(j))).pressed) { 
		//						((DashButton) (hud.elements.get(j))).pressed = false;
		//						if(!player.dashing && player.dashCD > player.DASH_CD_TIME) {
		//							player.dashing = true;
		//						}
		//					} 
		//				}
		//			}
		//		}
		if(!circlePressed) {
			for(int i = 0; i < hud.elements.size(); i++) {
				if(hud.elements.get(i).getClass() == DPadCircle.class) {
					//						System.out.println("reseting the circle");
					hud.elements.get(i).x = ((DPadCircle) hud.elements.get(i)).originX;
					hud.elements.get(i).y = ((DPadCircle) hud.elements.get(i)).originY;
				}
			}
		}
		if((NewGame.State == STATE.GAME || NewGame.State == STATE.BOSSFIGHT) && player.hp > 0) {
			if(!player.dashing && player.attackState == 0) { 
				if(Gdx.input.isKeyPressed(Keys.W)) {
					jumpSpeed = (float) (moveSpeed * 1.5); // change
					if(player.getJumping() < 2 && player.getVelY() > - jumpSpeed / (jumpSpeed - 1)) {
						if(player.getJumping() == 0) {
							player.playerJump.setCount(0); 
							player.playerDoubleJump.setCount(0);
							player.setVelY(- jumpSpeed);
							player.setJumping(player.getJumping() + 1); // fix this
							player.sounds.get(2).play(NewGame.gameVolume);
						} else {
							//							player.playerJump.setCount(0);
							player.setVelY((float) (- jumpSpeed / 1.2));
							player.setJumping(player.getJumping() + 1); // fix this
							player.sounds.get(5).play(NewGame.gameVolume);
						}
					}
				}
				if(Gdx.input.isKeyPressed(Keys.S) ) { 
					//					player.sprite.setPosition(player.sprite.getX(), player.sprite.getY() + moveSpeed);
				}
				if(Gdx.input.isKeyPressed(Keys.A)) { 
					//					player.sprite.setPosition(player.sprite.getX() - moveSpeed, player.sprite.getY());
					player.setVelX(-moveSpeed);
					player.sprite.setFlip(true, player.sprite.isFlipY());
				} else if(player.getVelX() < 0) {
					player.playerWalk.setCount(0);
					player.setVelX(0);
				}
				if(Gdx.input.isKeyPressed(Keys.D)) { 
					//					player.sprite.setPosition(player.sprite.getX() + moveSpeed * 5, player.sprite.getY()); //ZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAIZAI
					player.setVelX(moveSpeed);
					player.sprite.setFlip(false, player.sprite.isFlipY());
				} else if(player.getVelX() > 0) {
					player.playerWalk.setCount(0);
					player.setVelX(0);
				}
				if(Gdx.input.isKeyPressed(Keys.Q)) { 
					if(!player.dashing && player.dashCD > player.DASH_CD_TIME) {
						player.dashing = true;
					}
				}
			}
			if(Gdx.input.isKeyPressed(Keys.SPACE)) {
				//				handler.addObject(new PlayerProjectile(player.sprite.getX() + player.sprite.getWidth() / 2, player.sprite.getY() + player.sprite.getHeight() / 2, ObjectId.PlayerProjectile, handler, 0));
				if(player.attackState == 0) {
					if(player.getJumping() < 1) {
						player.setVelX(0);
					}
					player.attackState = 1;
					player.playerAttackOne.setCount(0);
					player.playerJumpAttack.setCount(0);
				} 	else if(player.attackState == 1 && player.playerAttackOne.getCount() > (int) (player.playerAttackOne.size() - player.playerAttackOne.size() / 5)) {
					if(!player.attackSound) { 

					}
					player.setVelX(0);
					player.attackingNext = true;
					player.playerAttackTwo.setCount(0);
				} else if(player.attackState == 2 && player.playerAttackTwo.getCount() > (int) (player.playerAttackTwo.size() - player.playerAttackTwo.size() - 5)) {
					player.setVelX(0);
					player.attackingNext = true;
					player.playerSlash.setCount(0);
				}
			}
			if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				System.exit(0);
			}
		}
	}



	public void render(float delta) {
//		mainSong.setVolume(NewGame.gameVolume);
		//		mainSong.play();
		update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(gameCam.combined);
		batch.begin();
		for(background bg : handler.backgrounds) {
			bg.draw(batch);
		}
		//		for (int i = 0; i < PlayScreen.renderList.size(); i++) {
		//			//			if (renderList.get(i).getId() == ObjectId.Background) {
		//			//				System.out.println("Rendering background object incorrectly from the PlayScreen render method around line 300 as of 9/15/16");
		//			//			}	
		//			GameObject tempObject = renderList.get(i);
		//			tempObject.draw(batch);
		//		}
		for(GameObject o: handler.object) {
			o.draw(batch);
		}
		player.draw(batch);
		for(int i = 0; i < hud.elements.size(); i++) {
			hud.elements.get(i).drawSprite(batch);
		}
		batch.end();	
		for(int i = 0; i < playSounds.size(); i++) {
			playSounds.get(i).play(NewGame.gameVolume);
			playSounds.remove(i);
		}
	}

	//	public static void myDispose() {
	//		batch.dispose();
	//	}

	public void resize(int width, int height) { gamePort.update(width, height);}
	public void show() {}
	public void pause() {}
	public void resume() {}
	public void hide() {}
	public void dispose() {batch.dispose();}

	public void setCamDeltas() {
		camDeltaX = (handler.camCenterX - gameCam.position.x);
		camDeltaY = (handler.camCenterY - gameCam.position.y);
		camDeltasSet = true;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
