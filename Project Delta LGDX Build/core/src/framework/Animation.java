package framework;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {

	public float speed; // time bewtween animation frames
	private int frames; // count of the total frames in the animation
	public float index = 0; // represents the time spent on the current frame
	private int count = 0; // frame count 
	public TextureRegion[] images; // array of animation frames
	public TextureRegion currentImg; // current frame to display
	public String name; // name that allows the Assets class to search for TextureRegions and Animations

	public Animation(float speed, AtlasRegion atlasRegion, int size, String name) {
		this.name = name;
		this.speed = speed;
		images = new TextureRegion[size];
		for (int i = 0; i < size; i++) {
			images[i] = grabFrame(atlasRegion, i + 1, 1, atlasRegion.getRegionWidth() / size, atlasRegion.getRegionHeight());
		}
		frames = size - 1;
		currentImg = images[0];
	}
	
	public Animation(Animation oldAnim) {
		this.name = oldAnim.name;
		this.speed  = oldAnim.speed;
		this.images = oldAnim.images;
		this.frames = oldAnim.size() - 1;
		this.currentImg = this.images[0];
	}

	public void runAnimation() {
		index += Gdx.graphics.getDeltaTime();
		if (index > speed) {
			index = 0;
				nextFrame();
		}
	}	 

	public void runAnimation(int time) {
		index += time;
		if (index > speed) {
			index = 0;
			nextFrame();
		}
	}	

	public void nextFrame() {
		count++;
		if (count > frames) {
			count = 0;
		}
		if(count > images.length - 1) {
			System.out.println(name + " is throwing out of bounds array exception, size: " + images.length + " count: " + count);
		}
		currentImg = images[count];
	}

	public TextureRegion drawAnimation(Graphics g, int x, int y) {
		return currentImg;
	}

	public int size() {
		return images.length;
	}

	public int getCount(){
		return count;
	}

	public void setCount(int i) {
		count = i;
	}

	public TextureRegion grabFrame(AtlasRegion atlasRegion, int col, int row, int width, int height) {
		return new TextureRegion(atlasRegion, (col * width) - width, (row * height) - height, width, height);
	}
}
