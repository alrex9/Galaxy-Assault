package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import framework.Assets;
import framework.NewGame;

public class UIObject {
	
	public Rectangle boundingRectangle;
	protected float scaleValue;
	public float x, y, width, height;
	public String[] names;
	public int index;
	public boolean clickable;
	
	public UIObject(float x, float y, float width, float height, boolean clickable, String ...args)	{
		this.height = height;
		this.width = width;
		universalConstructor(x, y, clickable, args);
	}
	
	public UIObject(float x, float y, boolean clickable, String ...args) {
		this.height = Assets.findRegion(args[0]).getRegionHeight();
		this.width = Assets.findRegion(args[0]).getRegionWidth();
		universalConstructor(x, y, clickable, args);		
	}

	protected void universalConstructor(float x, float y, boolean clickable, String ...args) {
		names = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            names[i] = args[i];
        }
        this.x = x;
        this.y = y + height;
        index = 0;
        this.clickable = clickable;
		setScale(1);
	}
	
	public void setScale(float scale) {
		scaleValue = scale;
		width *= scaleValue;
		height *= scaleValue;
		boundingRectangle = new Rectangle(x, y - height, width, height);
	}
	
	public void drawSprite(SpriteBatch batch) { 
		batch.draw(Assets.findRegion(names[index]), x, y, width, - height);
//		batch.draw(Assets.findRegion("BossBlock"), boundingRectangle.getX(), boundingRectangle.getY(), boundingRectangle.getWidth(), boundingRectangle.getHeight());
	}
	
	public boolean checkInput(Vector2 input) {
		if(boundingRectangle.contains((Vector2) input)) {
			pressed();
			return true;
		} else {
			index = 0;
			return false;
		}
	}
	
	public void pressed() {
//		System.out.println(names[index] + " was pressed at index: " + index);
		if(index + 1 < names.length) {
			index++;
		}
		Assets.findSound("ClickNoise.wav").play(NewGame.gameVolume);
	}
}
