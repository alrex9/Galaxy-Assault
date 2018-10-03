package framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import brashmonkey.spriter.Spriter;

import brashmonkey.spriter.gdx.Drawer;
import brashmonkey.spriter.gdx.Loader;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	public static String username;
	public static final AssetManager manager = new AssetManager();
	public static HashMap<String, TextureRegion> region = new HashMap<String, TextureRegion>();
	public static HashMap<String, Music> tracks = new HashMap<String, Music>();
	public static HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	public static final String[] names = {"Extras/Extras.txt"};
	public static TextureAtlas[] atlases = { new TextureAtlas()};
	public static ShapeRenderer renderer;
	public Loader loader;
	public Drawer drawer;
	static String[] animFiles = {"MainCharacterAnim/MainCharacterAnim.scml", "FlyingBossAnim/FlyingBossAnim.scml",
			"ExplosionAnim/ExplosionAnim.scml","RhinoAnim/RhinoAnim.scml", "SpinBladeAnim/SpinBladeAnim.scml", "SpoderAnim/SpoderAnim.scml",
			"SpoderBabyAnim/SpoderBabyAnim.scml", "TurtleAnin/TurtleAnin.scml"};
	public static int loadCounter = animFiles.length - 1;

	public static void preLoad() {
		System.out.println("init preload");
		FileHandle[] files;
		String dirPrefix = "";
		if(Gdx.app.getType() == Application.ApplicationType.Android) {
			files = Gdx.files.internal("FinishedSound/").list();
			dirPrefix = "FinishedSound/";
		} else {
			files = Gdx.files.internal("bin/FinishedSound/").list();
			dirPrefix = "bin/FinishedSound/";
		}
		System.out.println("Sound files: " + Arrays.toString(files));
		//System.exit(0);
		System.out.println("loading sounds");
		for(FileHandle file: files) {
			System.out.println("Sound name: " + file.path());
			if (!file.toString().contains("desktop.ini")) {
				//				System.out.println(file.toString().substring(20-1, file.toString().length()));
				sounds.put(file.toString().substring(dirPrefix.length(), file.toString().length()), Gdx.audio.newSound(file));
			}
		}
		if(Gdx.app.getType() == Application.ApplicationType.Android) {
			files = Gdx.files.internal("Music/").list();
			dirPrefix = "Music/";
		} else {
			files = Gdx.files.internal("bin/Music/").list();
			dirPrefix = "bin/Music/";
		}
		System.out.println("loading music");
		for(FileHandle file: files) {
			System.out.println("Music name: " + file.path());
			if (!file.toString().contains("desktop.ini")) {
				//				System.out.println(file.toString().substring(11, file.toString().length()));
				tracks.put(file.toString().substring(dirPrefix.length(), file.toString().length()), Gdx.audio.newMusic(file));
			}
		}
		renderer = new ShapeRenderer();
		renderer.setAutoShapeType(true);
		renderer.begin();
		Spriter.setDrawerDependencies(PlayScreen.batch, renderer);
		Spriter.init(Loader.class, Drawer.class);
	}

	public static void load() {
		System.out.println("loading...");
		for(int i = 0; i < names.length; i++) {
			manager.load(names[i], TextureAtlas.class);
			if(loadCounter >= 0) {
				Spriter.load(Gdx.files.internal(animFiles[loadCounter]).read(), animFiles[loadCounter]);
				loadCounter--;
			}
		}
		FileHandle dir = Gdx.files.internal("FinishedSounds");
		FileHandle[] directoryListing = dir.list();
	}

	public static void postLoad() {
//		for(String file: animFiles) {
//			System.out.println("Loading animation: " + file.toString());
//			Spriter.load(Gdx.files.internal(file).read(), file);
//		}
		System.out.println("init postload");
		for(int i = 0; i < names.length; i++) {
			atlases[i] = (Assets.manager.get(Assets.names[i], TextureAtlas.class));
		}
		System.out.println("parsing animations");
		parseAnimations();
	}

	public static void parseAnimations() {
		System.out.println("Parsing assets");
		for(int i = 0; i < names.length; i++) {
			FileHandle file = Gdx.files.internal(names[i]);
			Scanner sc = null;
			String line = "";
			sc = new Scanner(file.readString());
			while(!line.contains("repeat:")) {
				line = sc.nextLine();
			}
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				if(!line.startsWith(" ")) {
					region.put(line, atlases[i].findRegion(line));
				} else {
					line = sc.nextLine();
				}
			}
		}
	}

	public static TextureRegion findRegion(String s) {
		for(String str : region.keySet()) {
			if(str.equalsIgnoreCase(s)) {
				return region.get(str);
			}
		}
		System.out.println("Failed to find TextureRegion: " + s);
		return null;
	}

	public static Sound findSound(String s) {
		for(String sound: sounds.keySet()) {
			if (sound.equals(s)) {
				return sounds.get(sound);
			}
		}
		System.out.println("Failed to find Sound: " + s);
		return null;
	}

	public static Music findTrack(String s) {
		for(String music: tracks.keySet()) {
			if (music.equals(s)) {
				return tracks.get(music);
			}
		}
		System.out.println("Failed to find Music Track: " + s);
		return null;
	}

	public static void dispose() {
		manager.dispose();
		for(int i = 0; i < atlases.length; i++) {
			atlases[i].dispose();
		}
	}

	public TextureRegion grabFrame(AtlasRegion atlasRegion, int col, int row, int width, int height) {
		return new TextureRegion(atlasRegion, (col * width) - width, (row * height) - height, width, height);
	}

	public static void findUsername(File f, int level, String prevDir) {
		if(f.getName().equals("Google Drive") && level == 2) {
			username = prevDir;
		}
		if ((level < 2) && f.isDirectory() && !f.isHidden()) {
			for (File subF : f.listFiles()) {
				findUsername(subF, level + 1, f.getName());
			}
		}
	}
}
