package framework;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

import objects.Bat;
import objects.Block;
import objects.BossHandler;
import objects.BossOne;
import objects.BossThree;
import objects.BossTwo;
import objects.EasyTurret;
import objects.Enemy;
import objects.FinalBoss;
import objects.GameObject;
import objects.HardTurret;
import objects.Player;
import objects.Potion;
import objects.SpinningDisk;
import objects.Turtle;
import objects.UnfairTurret;
import objects.background;

public class Handler {

	public int levelStartX, levelStartY, levelEndX, levelEndY, runCount, blockCount, loadX, loadY, loadCount;
	public float camCenterX, camCenterY, nextLevelHeight, nextLevelWidth;
	public static String username;
	public LinkedList<GameObject> object;
	public ArrayList<GameObject> addList;
	public ArrayList<GameObject> disposeList;
//	public ArrayList<background> backgrounds;
	private ArrayList<BufferedImage> rngPlatforms;
	private BufferedImage boss_test, loadImg;
	private GameObject tempObject;
	private Random r;
	public Point levelStart;
	public OrthographicCamera gameCam;
	public static final int BLOCK_WIDTH = Assets.findRegion("MiddlePink").getRegionWidth();
	public static final int BLOCK_HEIGHT = Assets.findRegion("MiddlePink").getRegionHeight();
	public float deltaTime, bossCount;
	public boolean addingOrDisposing, loadingNewTerrain;
	public BlockPool pool;
	public Sprite background;
	public static int levelCount;

	public Handler(OrthographicCamera gameCam) throws FileNotFoundException {
		this.gameCam = gameCam;
		bossCount = 1;
		levelCount = 1; 
		loadingNewTerrain = true;
		object = new LinkedList<GameObject>();
		rngPlatforms = new ArrayList<BufferedImage>();
		addList = new ArrayList<GameObject>();
		disposeList = new ArrayList<GameObject>();
//		backgrounds = new ArrayList<background>();
		pool = new BlockPool();
		r = new Random();
		BufferedImageLoader loader = new BufferedImageLoader();
		username = "aearl";
		//		findUsername(new File("C:\\Users"), 0, null); 
		//		System.out.println(username);
//		username = Assets.username;
		System.out.println("username:" + username);
		System.out.println("looking for file");
		File file = new File("C:\\Users\\"+ username +"\\workspace\\Galaxy Assault\\android\\assets\\platforms.txt");
		//		C:\Users\aearl\workspace\Galaxy Assault\android\assets
		boss_test = loader.loadImage("/boss_test.png");
		Scanner sc = new Scanner(file);
		String name;
		while(sc.hasNextLine()){
			System.out.println("lul");
			name = sc.nextLine();
			System.out.println(name);
			rngPlatforms.add(loader.loadImage("/" + name + ".png"));
		}
		sc.close();
		addingOrDisposing = false;
		//		System.out.println("Block dimensions: " + BLOCK_WIDTH + ", " + BLOCK_HEIGHT);
//		background = new Sprite(Assets.findRegion("GameNebula"));
		System.out.println("handler init");
	}

	public void findUsername(File f, int level, String prevDir) {
		if(f.getName().equals("Google Drive") && level == 2) {
			username = prevDir;
		}
		if ((level < 2) && f.isDirectory() && !f.isHidden()) {
			for (File subF : f.listFiles()) {
				findUsername(subF, level + 1, f.getName());
			}
		}
	}

	public void tick(float dt) { 
		if(!loadingNewTerrain) {
			generatePresetBlocks(loadImg);
		} 
		//		System.out.println(loadingNewTerrain);
		deltaTime = dt;
		blockCount = 0;
//		for (int i = 0; i < backgrounds.size(); i++) {
//			backgrounds.get(i).tick();
//		}
		for (int i = 0; i < object.size(); i++) {
			//			if(object.get(i).getId() == ObjectId.Enemy) { // change to count any object
			//				blockCount ++;
			//			}
			tempObject = object.get(i);
			tempObject.tick(object);
		}
		//		System.out.println(blockCount);
		if(gameCam.position.x > nextLevelWidth - NewGame.V_WIDTH / 2 && loadingNewTerrain) {
//			levelStart = new Point((int) (levelStart.x + nextLevelWidth), (int) (levelStart.y + nextLevelHeight));
			generatePresetBlocks();
		}
		addingOrDisposing = true;
		for(int i = 0; i < disposeList.size(); i++) {
			this.removeObject(disposeList.get(i));
		}
		for(int i = 0; i< addList.size(); i++) {
			this.addObject(addList.get(i));
		}
		disposeList.clear();
		addList.clear();
		addingOrDisposing = false;
	}

	public void loadImageLevel(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight(); 
		nextLevelWidth = w * BLOCK_WIDTH;
		nextLevelHeight = 0;
		levelStart = new Point((int) (nextLevelHeight), (int) (nextLevelWidth));
//		backgrounds.add(new background(gameCam.position.x - NewGame.V_WIDTH / 2f, gameCam.position.y + NewGame.V_HEIGHT, this, ObjectId.Background, false, true));
//		backgrounds.add(new background(gameCam.position.x - NewGame.V_WIDTH / 2f, gameCam.position.y + NewGame.V_HEIGHT - background.getRegionHeight(), this, ObjectId.Background, false, false));
//		backgrounds.add(new background(gameCam.position.x - NewGame.V_WIDTH / 2f + background.getRegionWidth(), gameCam.position.y + NewGame.V_HEIGHT, this, ObjectId.Background, true, true));
//		backgrounds.add(new background(gameCam.position.x - NewGame.V_WIDTH / 2f + background.getRegionWidth(), gameCam.position.y + NewGame.V_HEIGHT - background.getRegionHeight(), this, ObjectId.Background, true, false));
		for (int xx = 0; xx < w; xx++) {
			for (int yy = 0; yy < h; yy++) {
				int pixel = image.getRGB(xx, yy);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				if(!(red == 0 && green == 0 && blue == 0)) {
					// System.out.println(red + " " + green + " " + blue);
					// System.out.println(xx + " " + yy);
					if (red == 255 && green == 58 && blue == 235) { // pink left
						Block block = pool.obtain();
						block.sprite.setPosition(xx * BLOCK_WIDTH, yy * BLOCK_HEIGHT);
						block.sprite.setRegion(Assets.findRegion("LeftPink"));
						block.flipX = false;
						addList.add(block);
					} else if (red == 255 && green == 153 && blue == 248) { // pink middle
						Block block = pool.obtain();
						block.sprite.setPosition(xx * BLOCK_WIDTH, yy * BLOCK_HEIGHT);
						block.sprite.setRegion(Assets.findRegion("MiddlePink"));
						block.flipX = false;
						addList.add(block);
					} else if (red == 174 && green == 0 && blue == 255) { // pink right	
						//						System.out.println("adding right plat");
						Block block = pool.obtain();
						block.sprite.setPosition(xx * BLOCK_WIDTH, yy * BLOCK_HEIGHT);
						block.sprite.setRegion(Assets.findRegion("LeftPink"));
						block.flipX = true;
						addList.add(block);
					} else if(red == 255 && green == 0 && blue == 0){ // regular enemy
						//	                    addObject(new Enemy(xx * BLOCK_WIDTH, yy * BLOCK_H, this,ObjectId.Enemy, NewGame.playScreen));
					} else if (red == 0 && green == 0 && blue == 255) { // player spawn (blue)
						// addObject(new Player(xx * BLOCK_WIDTH, yy * BLOCK_HEIGHT, this, ObjectId.Player, (PlayScreen) NewGame.playScreen));
						System.out.println("player created");
						PlayScreen.player = new Player(xx * BLOCK_WIDTH, yy * BLOCK_HEIGHT, this, ObjectId.Player);
					} else if (red == 255 && green == 216 && blue == 0) { // flag (yellow)
						//addObject(new Flag(xx * BLOCK_WIDTH, yy * BLOCK_HEIGHT, ObjectId.Flag));
						addObject(new Block(xx * BLOCK_WIDTH, yy * BLOCK_HEIGHT, 2, this, ObjectId.Block));
					} else if (red == 0 && green == 255 && blue == 255) { // level start (yellow)
						addObject(new Block(xx * BLOCK_WIDTH, yy * BLOCK_HEIGHT, 0,this, ObjectId.Block));
					}
				}
			}
		}
	}

	private void generatePresetBlocks(BufferedImage image) { // generates blocks at the correct position relative to the players spot
		int w = loadImg.getWidth();
		int h = loadImg.getHeight();
		for (int yy = 0; yy < loadY; yy++) {
			int pixel = image.getRGB(loadX, yy);
			int red = (pixel >> 16) & 0xff;
			int green = (pixel >> 8) & 0xff;
			int blue = (pixel) & 0xff;
			float x = (float) (loadX * BLOCK_WIDTH) + nextLevelWidth;
			float y = (float) (yy * BLOCK_HEIGHT) - nextLevelHeight - h * BLOCK_HEIGHT + BLOCK_HEIGHT;
			if (red == 255 && green == 58 && blue == 235) { // pink left
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("LeftPink"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 255 && green == 153 && blue == 248) { // pink middle
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("MiddlePink"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 174 && green == 0 && blue == 255) { // pink right
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("LeftPink"));
				block.flipX = true;
				addList.add(block);
			} else if (red == 1 && green == 1 && blue == 1) { // purple left
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("LeftPurple"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 1 && green == 2 && blue == 1) { // purple middle
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("MiddlePurple"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 1 && green == 3 && blue == 1) { // purple right
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("LeftPurple"));
				block.flipX = true;
				addList.add(block);
			}  else if (red == 2 && green == 1 && blue == 1) { // blue left
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("LeftBlue"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 2 && green == 2 && blue == 1) { // blue middle
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("MiddleBlue"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 2 && green == 3 && blue == 1) { // blue right
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("LeftBlue"));
				block.flipX = true;
				addList.add(block);
			} else if (red == 3 && green == 1 && blue == 1) { // green left
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("LeftGreen"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 3 && green == 2 && blue == 1) { // green middle
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("MiddleGreen"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 3 && green == 3 && blue == 1) { // green right
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("LeftGreen"));
				block.flipX = true;
				addList.add(block);
			} else if (red == 4 && green == 1 && blue == 1) { // Boss block
				Block block = pool.obtain();
				block.sprite.setPosition(x, y);
				block.sprite.setRegion(Assets.findRegion("BossBlock"));
				block.flipX = false;
				addList.add(block);
			} else if (red == 254 && green == 254 && blue == 254) { // Boss block
				addList.add(new Potion(x, y, this, ObjectId.Potion));
			}else if (red == 255 && green == 0 && blue == 0) { // regular enemy spawn (red)
				addList.add(new Enemy(x, y, this, ObjectId.Enemy));
			} else if(red == 255 && green == 122 && blue == 248) {
				addList.add(new Bat(x, y, this, ObjectId.Enemy));
			} else if(red == 40 && green == 255 && blue == 251) {
				addList.add(new SpinningDisk(x, y, this, ObjectId.Enemy));
			} else if(red == 127 && green == 106 && blue == 0) {
				addList.add(new Turtle(x, y, this, ObjectId.Enemy));
				//				} else if(red == 77 && green == 1 && blue == 1) {
				//					addList.add(new BossOne(x, y, this,ObjectId.Enemy));
				//				} else if(red == 77 && green == 2 && blue == 1) {
				//					addList.add(new BossTwo(x, y, this,ObjectId.Enemy));
				//				} else if(red == 77 && green == 3 && blue == 1) {
				//					addList.add(new BossThree(x, y, this,ObjectId.Enemy));
				//				} else if(red == 77 && green == 4 && blue == 1) {
				//					addList.add(new FinalBoss(x, y, this,ObjectId.Enemy));
			} else if(red == 255 && green == 216 && blue == 0) { // Boss handler (gold)
				addList.add(new BossHandler(x, y, this, ObjectId.BossHandler));
			} else if (red == 255 && green == 0 && blue == 102) { // boss spawn (pink)
				if(bossCount == 1) {
					System.out.println("adding boss one");
					addList.add(0, new BossOne(x, y, this,ObjectId.Enemy));
				} else if(bossCount == 2) {
					System.out.println("adding boss two");
					addList.add(new BossTwo(x, y, this,ObjectId.Enemy));
				} else if(bossCount == 3) {
					System.out.println("adding boss three");
					addList.add(new BossThree(x, y, this,ObjectId.Enemy));
				} else if(bossCount == 4) {
					System.out.println("adding final boss");
					addList.add(new FinalBoss(x, y, this,ObjectId.Enemy));
				} else {
					System.out.println("the game probably broke in the handler class trying to spawn a boss with boss count: " + this.bossCount);
				}
			} else if (red == 0 && green == 255 && blue == 12) { // easy turret up spawn (neon green)
				addList.add(new EasyTurret(x, y, 0, true, this,ObjectId.Enemy));
			} else if (red == 255 && green == 17 && blue == 251) { // hard turret up spawn (neon purple)
				addList.add(new HardTurret(x, y, 1, true, this,ObjectId.Enemy));
			} else if (red == 0 && green == 255 && blue == 255) { // unfair turret up spawn (neon blue)
				addList.add(new UnfairTurret(x, y, 2, true, this,ObjectId.Enemy));
			} else if (red == 0 && green == 116 && blue == 12) { // hard turret down spawn (neon green)
				addList.add(new EasyTurret(x, y, 0, false, this,ObjectId.Enemy));
			} else if (red == 191 && green == 0 && blue == 255) { // easy turret down spawn (neon purple)
				addList.add(new HardTurret(x, y, 1, false, this,ObjectId.Enemy));
			} else if (red == 35 && green == 141 && blue == 255) { // unfair turret down spawn (neon blue)
				addList.add(new UnfairTurret(x, y, 2, false, this,ObjectId.Enemy));
			}else if (red == 255 && green == 171 && blue == 5) { // camera center location (orange)
				camCenterX = x;
				camCenterY = y - (NewGame.V_HEIGHT / 2) + (BLOCK_HEIGHT * 4);
			} else if (red == 178 && green == 19 && blue == 38) { // fire block? (maroon)
				addList.add(new Block(x, y, 2, this, ObjectId.Block));
			} else if (red == 128 && green == 128 && blue == 128) { // cloud (gray)
				//addList.add(new BackgroundObject(x, y,, ObjectId.BackgroundObject));
			}
		}
		loadX++;
		if(loadX == loadImg.getWidth()) {
			loadingNewTerrain = true;
			System.out.println("new terrain loaded");
			nextLevelWidth += loadImg.getWidth() * BLOCK_WIDTH;
			nextLevelHeight += loadImg.getHeight() * BLOCK_HEIGHT;
		} else {
//			System.out.println("didnt make it: " + loadX);
		}
	}

	public void loadImage(BufferedImage image) {
		loadImageLevel(image);
	}
	public LinkedList<GameObject> getLL(){
		return this.object;
	}

	public void generatePresetBlocks() {
		System.out.println("generating more level area");
		if(levelCount % 2 == 0) {
			loadImg = boss_test;
		} else {
			loadImg = rngPlatforms.get(r.nextInt(rngPlatforms.size()));
		}
		loadingNewTerrain = false;
		loadX = 0;
		loadY = loadImg.getHeight();
		//		generatePresetBlocks(image);
		//		nextLevelWidth += loadImg.getWidth() * BLOCK_WIDTH;
		//		nextLevelHeight += loadImg.getHeight() * BLOCK_HEIGHT;
//		System.out.println(nextLevelWidth + ", " + nextLevelHeight);
		levelCount++;
	}

	public void addObject(GameObject object){
		//				if(!addingOrDisposing){
		//					System.out.println("adding " + object.getId());
		//		}
		this.object.add(object);
	}
	public void removeObject(GameObject object){
		//		if(!addingOrDisposing){
		//			System.out.println("removing " + object.getId());
		//		}
		if(object.getClass() == Enemy.class) {
			((Enemy) (object)).dispose();
		}
		this.object.remove(object);

	}
	public void clearLevel(){
		object.clear();
	}

	public class BlockPool extends Pool<Block> {

		@Override
		protected Block newObject() {
			return new Block(0,0,0, Handler.this, ObjectId.Block);
		}
	}
}
