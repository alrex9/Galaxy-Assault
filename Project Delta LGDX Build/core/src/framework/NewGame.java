/*
package framework;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NewGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

*/
package framework;

		import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;

public class NewGame extends Game {

//	public final static String productID_buy1HP = "Buy1HP";
//	public final static String productID_buy3HP = "Buy3HP";
//	public final static String productID_buySword1 = "BuySword1";
//	public final static String productID_buySword2 = "BuySword2";
//	public final static String productID_buySword3 = "BuySword3";
//	public final static String productID_buySword4 = "BuySword4";
//
//	public final static String[] productIDs= {productID_buy1HP, productID_buy3HP,productID_buySword1,productID_buySword2,productID_buySword3,productID_buySword4};
//
//	// ----- app stores -------------------------
//	public static final int APPSTORE_UNDEFINED = 0;
//	public static final int APPSTORE_GOOGLE = 1;
//	public static final int APPSTORE_OUYA = 2;
//	public static final int APPSTORE_AMAZON = 3;
//	public static final int APPSTORE_DESKTOP = 4;
//
//	public static final int ISAPPSTORE = APPSTORE_GOOGLE;

	public final static float VOLUME_DIVIDER = 0.2f;
	public static float gameVolume;
	public static  int V_WIDTH;
	public static  int V_HEIGHT;
	public static float BASE_SPEED;
	public static boolean playScreenLoaded;
	public static final float BASE_SPEED_MULTIPLYER = 700;
	public static int volumeMultiplyer = 3;
	// 0 - hp, 1 - volume, 2 - potions, 3 - sword1, 4 - sword2, 5 - sword3, 6 - sword4
	//	public static int playerHp, playerPotions;
	//	public static boolean swordOnePurchased, swordTwoPurchased, swordThreePurchased, swordFourPurchased;
//	public static File inputFile;
	public static Preferences playerProfile;
	public static PrintStream prntStrm;
	public static PrintWriter wrtr;
	public static PlayScreen playScreen;
	public static MenuScreen menuScreen;
	public static LoadScreen loadScreen;
	//	public Screen[] screens;
	public static Screen currentScreen;

//	public static PlatformResolver m_platformResolver;
//	public static PurchaseManagerConfig purchaseManagerConfig;
//	public static PurchaseObserver purchaseObserver = new PurchaseObserver() {
//
//		@Override
//		public void handleRestore (Transaction[] transactions) {
//			for (int i = 0; i < transactions.length; i++) {
//				if (checkTransaction(transactions[i].getIdentifier()) == true) break;
//			}
//		}
//
//		@Override
//		public void handleRestoreError (Throwable e) {
//			// getPlatformResolver().showToast("PurchaseObserver: handleRestoreError!");
//			Gdx.app.log("ERROR", "PurchaseObserver: handleRestoreError!: " + e.getMessage());
//			throw new GdxRuntimeException(e);
//		}
//
//		@Override
//		public void handleInstall () {
//			//	         getPlatformResolver().showToast("PurchaseObserver: installed successfully...");
//			Gdx.app.log("handleInstall: ", "successfully..");
//		}
//
//		@Override
//		public void handleInstallError (Throwable e) {
//			//	         getPlatformResolver().showToast("PurchaseObserver: handleInstallError!");
//			Gdx.app.log("ERROR", "PurchaseObserver: handleInstallError!: " + e.getMessage());
//			throw new GdxRuntimeException(e);
//		}
//
//		@Override
//		public void handlePurchase (Transaction transaction) {
//			checkTransaction(transaction.getIdentifier());
//		}
//
//		@Override
//		public void handlePurchaseError (Throwable e) {
//			if (e.getMessage().equals("There has been a Problem with your Internet connection. Please try again later")) {
//
//				// this check is needed because user-cancel is a handlePurchaseError too)
//				// getPlatformResolver().showToast("handlePurchaseError: " + e.getMessage());
//			}
//			throw new GdxRuntimeException(e);
//		}
//
//		@Override
//		public void handlePurchaseCanceled () {
//		}
//	};

//	public static boolean checkTransaction (String ID) {
//		for(String s : productIDs) {
//			//			System.out.println(s + " " + ID);
//			if (s.equals(ID)) {
//				System.out.println("Purhcased: " + ID);
//				return true;
//			}
//		}
//		return false;
//	}

	public void create () {
		System.out.println("NewGame Created");
		//		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		//		System.out.println((float)Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
		//		float height = (float) Gdx.graphics.getHeight();
		//		float width = (float) Gdx.graphics.getWidth();
		//		BASE_SPEED = ((width) / height);
		V_WIDTH = 2560;
		V_HEIGHT = 1440;
		//		Assets.load();
		//		Assets.manager.finishLoading();;
		//		while(!Assets.manager.update()){
		//			System.out.println(Assets.manager.getProgress() * 100 + "%");
		//			Assets.manager.update();
		//		}
		//		Assets.postLoad();
		BASE_SPEED = BASE_SPEED_MULTIPLYER * (float) (2560 / 1440);
		gameVolume = volumeMultiplyer * VOLUME_DIVIDER;
		System.out.println("init preferences");
		NewGame.playerProfile = Gdx.app.getPreferences("com.isigdri.GalaxyAssault");
		if(!playerProfile.contains("hp")) {
			playerProfile.putInteger("hp", 3);
			playerProfile.putInteger("volume", 3);
			playerProfile.putInteger("potions", 0);
			playerProfile.putInteger("BossSword1", 0);
			playerProfile.putInteger("BossSword2", 0);
			playerProfile.putInteger("BossSword3", 0);
			playerProfile.putInteger("BossSword4", 0);
//			hp	volume	potions	Bossword1	BossSword2	BosSword3	BossSword 4
		}
		//System.out.println(BASE_SPEED);
		try { menuScreen = new MenuScreen(this);
		} catch (FileNotFoundException e1) {}
		loadScreen = new LoadScreen(this);
		//		screens = new Screen[3];
		//		screens[0] = menuScreen;
		//		screens[1] = playScreen;
		currentScreen = menuScreen;
		setScreen(loadScreen);
		State = STATE.MENU;
		// ---- IAP: define products ---------------------
//		purchaseManagerConfig = new PurchaseManagerConfig();
//		for(String s : productIDs) {
//			purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier(s));
//		}
	}

	public static enum STATE {
		MENU,
		MAP,
		GAME,
		GAMEMENU,
		TRANSITIONTOBOSS,
		BOSSFIGHT,
		TRANSITIONFROMBOSS,
		BOSSSWORD
	};

	public static STATE State = STATE.GAME;

	public void render () {
		super.render();
		gameVolume = volumeMultiplyer * VOLUME_DIVIDER;
		//		updatesetScreen();
		if(State == STATE.GAME && currentScreen != playScreen) {
			if(!playScreenLoaded) {
				System.out.println("creating new playScreen");
				try {
					playScreen = new PlayScreen(this);
				} catch (FileNotFoundException e1) {}
				playScreenLoaded = true;
			}
			setScreen(playScreen);
		} else if(State == STATE.MENU && Assets.manager.getProgress() == 1) {
			setScreen(menuScreen);
		}
	}
	public void dispose() {
		super.dispose();
		Assets.dispose();
	}

	public void resize(int width, int height) {

	}

	public void pause() {
		// TODO Auto-generated method stub

	}

	public void resume() {
		// TODO Auto-generated method stub

	}

	public void updatesetScreen() {
		if(State == STATE.GAME) {
			setScreen(playScreen);
		} else if(State == STATE.MENU) {
			setScreen(menuScreen);
		}
	}

//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//
//public class NewGame extends ApplicationAdapter {
//	SpriteBatch batch;
//	TextureRegion img;
//
//	@Override
//	public void create () {
//		Assets.load();
//		Assets.manager.finishLoading();
//		Assets.postLoad();
//		batch = new SpriteBatch();
////		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Enemies&Other.txt"));
////		img = Assets.regions.get(76);
//		img = Assets.findRegion("LeftGreen");
//	}
//
//	@Override
//	public void render () {
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 100, 100);
//		batch.end();
//	}
//
//	@Override
//	public void dispose () {
//		batch.dispose();
//	}
}
