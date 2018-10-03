package framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TwoDAnimation {

	public float index, speed, size;
	public TextureRegion[][] images;
	public int secondCount, count, totalCount;
	public boolean completeOnce;
	public TextureRegion currentImg;

	public TwoDAnimation(TextureRegion[][] tex) {
		index = 0;
		count = 0;
		this.speed = .025f;
		this.images = tex;
		this.currentImg = images[0][0];
		size = images[0].length + 1 * images.length + 1;
	}
	
	public void runAnimation() {
		index += Gdx.graphics.getDeltaTime();
		if (index > speed) {
			index = 0;
			nextFrame();
		}
	}	 

	public void nextFrame() {
		secondCount++;
		totalCount++;
		if(secondCount > images[count].length - 1) {
			count++;
			if(count > images.length - 1) {
				secondCount--;
				completeOnce = true;
				totalCount = 0;
				count = images.length - 1;
			} else {
				secondCount = 0;
			}
		}
		currentImg = images[count][secondCount];
	}
	
	public void reset() {
		count = 0;
		secondCount = 0;
		totalCount = 0;
		index = 0;
	}
}
