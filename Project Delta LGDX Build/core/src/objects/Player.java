package objects;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import brashmonkey.spriter.GDXPlayer;
import brashmonkey.spriter.Spriter;
import framework.Assets;
import framework.Handler;
import framework.NewGame;
import framework.NewGame.STATE;
import framework.ObjectId;
import framework.PlayScreen;

public class Player extends GameObject {
	
	public float width, height, takeDamageTime;
	protected float gravity, waitTime, defaultWaitTime, scaleValue; // 0 = not jumping, 1 = jumped once, 2 = jump twice
	protected float damageTimer, velX, velY, jumping;
	public float hp;
	public GDXPlayer sprite;
	public final float MAX_SPEED = (NewGame.BASE_SPEED * 1.5f);
	public final float DASH_SPEED = NewGame.BASE_SPEED * 5;
	public final float DASH_TRAVEL_DISTANCE = NewGame.V_WIDTH / 4;
	public final float DASH_CD_TIME = .75f;
	public boolean dashing, attackingNext, movedRight, movedLeft, soundFired, runSound, attackSound, damagedSound, gameOver, flipX;
	public float dashCD, dashDistance, gameOverCount;
	public ArrayList<Sound> sounds;
	public int attackState;
	public Rectangle topBounds, bottomBounds, leftBounds, rightBounds, boundingRectangle;

	public Player(float x, float y, Handler handler, ObjectId id) {
		super(x, y, handler, id);
		sprite = Spriter.newPlayer("MainCharacterAnim/MainCharacterAnim.scml", 0);
		takeDamageTime = 1f;
		sounds = new ArrayList<Sound>();
		sounds.add(Assets.findSound("MainCharacterWalk.wav"));
		sounds.add(Assets.findSound("PlayerInjury.wav"));
		sounds.add(Assets.findSound("Jump.wav"));
		sounds.add(Assets.findSound("SwordCollision.wav"));
		sounds.add(Assets.findSound("SwordSwings.wav"));
		sounds.add(Assets.findSound("DoubleJump.wav"));
		//		sounds.add(Assets.findSound(""));
		//		sounds.add(Assets.findSound(""));
		//		sounds.add(Assets.findSound(""));
//		dashAnim.speed = 0.008777f;
		attackState = 0;
		setScale(0.25f);
		hp = NewGame.playerProfile.getInteger("hp");
	}	   

	public void tick(LinkedList<GameObject> object) {
		move();
		if (falling || jumping != 0) {
			velY += gravity * handler.deltaTime;
			if (velY > MAX_SPEED) {
				velY = MAX_SPEED;
			}
		}
		Collision(object);
		takeDamage();
		checkDash();
	}

	public void checkDash() {
		if(dashing) {
			sprite.setAnimation("Dash");
			if(dashDistance < DASH_TRAVEL_DISTANCE) {
				velY = 0;
				if(!flipX) {
					velX = DASH_SPEED;
				} else {
					velX = -DASH_SPEED; 
				}
				dashDistance += DASH_SPEED * handler.deltaTime;
			} else {
				velX = 0;
				dashDistance = 0;
				dashCD = 0;
				dashing = false; 
				sprite.setAnimation("Idle");
			}
		} else {
			if(dashCD < DASH_CD_TIME) {
				dashCD += handler.deltaTime;
			}
		}
	}

	public void Collision(LinkedList<GameObject> object) {
		calculateBounds();
		for (int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = object.get(i);
			if (tempObject.getId() == ObjectId.Block && Math.abs(tempObject.x - sprite.getX()) < NewGame.V_WIDTH / 5 && Math.abs(tempObject.y - sprite.getY()) < NewGame.V_HEIGHT / 4) {
				if (bottomBounds.overlaps(tempObject.boundingRectangle)) {
					sprite.setPosition(sprite.getX(), tempObject.y + 1);
					velY = 0;
					falling = false;
					jumping = 0;
				} else {
					falling = true;
				}
				if (topBounds.overlaps(tempObject.boundingRectangle)) { 
					movedLeft = true;
					movedRight = true;
					sprite.setPosition(sprite.getX(), tempObject.y + tempObject.height + height + 1);
					velY = 0; 
				}
				if (!movedLeft && rightBounds.overlaps(tempObject.boundingRectangle)) {
					movedLeft = true;
					sprite.setPosition(tempObject.x - width - 1, sprite.getY());
				} 
				if (!movedRight && leftBounds.overlaps(tempObject.boundingRectangle)) {
					movedRight = true; 
					sprite.setPosition(tempObject.x + tempObject.width + 1,  sprite.getY());
				}
			} else if(tempObject.getId() == ObjectId.Enemy) {
				if(tempObject.boundingRectangle.overlaps(this.boundingRectangle)) {
					if(attackState > 0 && !tempObject.takingDamage) {
						if(!((Enemy) (tempObject)).innerRectangleOnly && ((!flipX && rightBounds.overlaps(tempObject.boundingRectangle)) || 
								(flipX && leftBounds.overlaps(tempObject.boundingRectangle)))) { 
							sounds.get(3).play(NewGame.gameVolume);
							tempObject.takingDamage = true;
							tempObject.hp--;
						} else if (((Enemy) (tempObject)).innerRectangleOnly && ((!flipX && rightBounds.overlaps(((Enemy) (tempObject)).constantDamageBox) || 
								(flipX && leftBounds.overlaps(((Enemy) (tempObject)).constantDamageBox))))) {
							sounds.get(3).play(NewGame.gameVolume);
							tempObject.takingDamage = true;
							tempObject.hp--;
						}
					}
				}
			} else if(tempObject.getId() == ObjectId.EnemyProjectile) {
				if (this.boundingRectangle.overlaps(tempObject.boundingRectangle)) {
					if(!takingDamage && !dashing && attackState == 0 && hp > 0 && (NewGame.State == STATE.GAME || NewGame.State == STATE.BOSSFIGHT)) {
						takingDamage = true;
						hp--;
					} 
					handler.disposeList.add(tempObject);
				}
			}
		}
		movedRight = false;
		movedLeft = false;
	}

	private void calculateBounds() {
		topBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height, width / 2, idleFrame.getRegionHeight() * scaleValue / 4);
		bottomBounds = new Rectangle(sprite.getX() + (width / 4), sprite.getY() - height + (height * 3 / 4), width / 2, height / 4);
		leftBounds = new Rectangle( sprite.getX(), sprite.getY() - height + height  / 24, width / 4, height / 1.2f);
		rightBounds = new Rectangle(sprite.getX() + width * 3 / 4, sprite.getY() - height + height / 24, width / 4, height / 1.2f);
	}

	public void draw(SpriteBatch batch) {
		if (hp <= 0) {
			velX = 0;
			takingDamage = false;
			if(sprite.addListener(listener);) {
				deathAnim.runAnimation();
				animationState = 7;
				sprite.setRegion(deathAnim.currentImg);
				damageTimer = 2;
			} else {
				damageTimer -= handler.deltaTime;
				if(damageTimer < 0) {
					gameOver = true;
					// gameover scree
					// dispose of current playScreen
					// show ads
					// show menu
				}
			}
		} else if(dashing) {
			animationState = 8;
			if (dashAnim.getCount() != dashAnim.size()) {
				dashAnim.runAnimation();
			} 
			sprite.setRegion(dashAnim.currentImg);
		} else if(Math.abs(velX) > 0 && velY == 0) {
			playerWalk.runAnimation();
			animationState = 0;
			sprite.setRegion(playerWalk.currentImg);
			if(attackState > 0) {
				attackState = 0;
			}
		} else if(attackState > 0 && (jumping > 0)) { // jump attack currently does not work
			if(playerJumpAttack.getCount() == playerJumpAttack.size() - 1) {
				//				sprite.setRegion(textures.player_jump_attack[playerJumpAttack.size() - 1]);
				sprite.setRegion(playerJumpAttack.images[playerJumpAttack.size() - 1]);
				playerJumpAttack.setCount(0);
				animationState = 6;
				attackState = 0;
			} else {
				playerJumpAttack.runAnimation();
				animationState = 6;
				sprite.setRegion(playerJumpAttack.currentImg);
			}
		} else if(attackState > 0) {
			if(attackState == 1) {
				playerAttackOne.runAnimation();
				animationState = 3;
				sprite.setRegion(playerAttackOne.currentImg);
				if(playerAttackOne.getCount() == playerAttackOne.size() - 1 && playerAttackOne.index >= playerAttackOne.speed / 2) {
					if(attackingNext) {
						attackState = 2;
						attackSound = false;
					} else {
						attackState = 0;
						attackingNext = false;
					}
				}
			} else if(attackState == 2) {
				playerAttackTwo.runAnimation();
				animationState = 4;
				sprite.setRegion(playerAttackTwo.currentImg);
				if(playerAttackTwo.getCount() == playerAttackTwo.size() - 1 && playerAttackTwo.index >= playerAttackTwo.speed / 2) {
					if(attackingNext) {
						attackState = 3;
						attackSound = false;
					} else {
						attackState = 0;
						attackingNext = false;
					}
				}
			} else if(attackState == 3) {
				playerSlash.runAnimation();
				animationState = 5;
				sprite.setRegion(playerSlash.currentImg);
				if(playerSlash.getCount() == playerSlash.size() - 1 && playerSlash.index >= playerSlash.speed / 2) {
					attackSound = false;
					attackState = 0;
					attackingNext = false;
				}
			}
		} else if(jumping == 1 && attackState == 0) {
			int tempCount = playerJump.getCount();
			if(tempCount != playerJump.size() - 1) {
				playerJump.runAnimation();
			}
			animationState = 1;
			if(tempCount == playerJump.size() - 1) {
				//				sprite.setRegion(textures.player_jump[playerJump.size() - 1]);
				sprite.setRegion(playerJump.images[playerJump.size() - 1]);
				playerJump.setCount(playerJump.size() - 1);
			} else {
				sprite.setRegion(playerDoubleJump.images[playerDoubleJump.size() - 1]);	
			}
		} else if(jumping == 2 && attackState == 0) {
			int tempCount = playerDoubleJump.getCount();
			playerDoubleJump.runAnimation();
			animationState = 2;
			if(tempCount == playerDoubleJump.size() - 1) {
				//				sprite.setRegion(textures.player_double_jump[playerDoubleJump.size() - 1]);	
				sprite.setRegion(playerDoubleJump.images[playerDoubleJump.size() - 1]);	
				playerDoubleJump.setCount(playerDoubleJump.size() - 1);
			} else {
				sprite.setRegion(playerJump.images[playerJump.size() - 1]);
			}
		} else {
			animationState = 9;
			sprite.setRegion(idleFrame);
		}	
		sprite.setFlip(flipX, true);
		if(!runSound && animationState == 0) { 
			runSound = true;
			sounds.get(0).play(NewGame.gameVolume);
		} else if (animationState!= 0) {
			runSound = false;
			sounds.get(0).stop();
		}
		if(!attackSound && attackState > 0) {
			sounds.get(4).play(NewGame.gameVolume);
			attackSound = true;
		} else if (attackState <= 0) { 
			attackSound = false;
		}
		drawSprite(batch);
		if(gameOver) { 
			if(gameOverCount > 3) {
				// 				PlayScreen.myDispose();
				PlayScreen.hud.elements.clear();
				handler.backgrounds.clear();
				//ads here
				NewGame.State = STATE.MENU;
				NewGame.menuScreen.paused = false;
				for(GameObject o : handler.object) { 
					handler.disposeList.add(o);
				}
			}
			batch.draw(Assets.findRegion("Gameover"), handler.gameCam.position.x - 800, handler.gameCam.position.y - 400, 1600, 800);
			gameOverCount += Gdx.graphics.getDeltaTime();
		}
	}

	public void takeDamage() {
		if(takingDamage && damageTimer < takeDamageTime) {
			damageTimer += Gdx.graphics.getDeltaTime();
			if(!damagedSound) {
				damagedSound = true;
				sounds.get(1).play(NewGame.gameVolume);
			}
		} else {
			damagedSound = false;
			takingDamage = false;
			damageTimer = 0;
		}
	}

	public void drawSprite(SpriteBatch batch) {
		if(animationState != 9) {
			width = animations[animationState].currentImg.getRegionWidth() * scaleValue;
			height = animations[animationState].currentImg.getRegionHeight() * scaleValue;
			sprite.setRegion(animations[animationState].currentImg);
		} else {
			sprite.setRegion(idleFrame);
			width = idleFrame.getRegionWidth() * scaleValue;
			height = idleFrame.getRegionHeight() * scaleValue;
		}
		if(takingDamage) {
			batch.setColor(Color.RED);
		}
		sprite.setFlip(flipX, true);
		batch.draw(sprite, sprite.getX(), sprite.getY() - height, width, height);
		batch.setColor(Color.WHITE);
		//		batch.draw(Assets.findRegion("BossBlock"), topBounds.getX(), topBounds.getY(), topBounds.getWidth(), topBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), bottomBounds.getX(), bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), leftBounds.getX(), leftBounds.getY(), leftBounds.getWidth(), leftBounds.getHeight());
		//		batch.draw(Assets.findRegion("BossBlock"), rightBounds.getX(), rightBounds.getY(), rightBounds.getWidth(), rightBounds.getHeight());
		boundingRectangle = new Rectangle(sprite.getX(), sprite.getY() - height, width,  height);
		//					batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
}
