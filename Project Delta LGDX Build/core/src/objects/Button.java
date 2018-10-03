package objects;

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import framework.BufferedImageLoader;


public class Button {

    private float x;
    private float y;
    public int width;
    public int height;
    private int pointerLocationIntX;
    private int pointerLocationIntY;
    private String type;
    private BufferedImage resting;
    private BufferedImage hovering;
    private BufferedImage clicked;
    private BufferedImage currentImg;
    private PointerInfo a;
    private Point b;
    private BufferedImageLoader loader = new BufferedImageLoader();

    private boolean mousePressed;

    private int buttonImgState = 1;

    public Button(float x, float y, String resting, String hovering, String clicked, String type) {

        this.type = type;

        this.resting = loader.loadImage(resting);
        this.hovering = loader.loadImage(hovering);
        this.clicked = loader.loadImage(clicked);

        this.x = x;
        this.y = y;

        this.width = this.resting.getWidth();
        this.height = this.resting.getHeight();

        this.mousePressed = false;

        this.currentImg = this.resting;

    }

    public void render(Graphics g) {
        a = MouseInfo.getPointerInfo();
        b = a.getLocation();
        pointerLocationIntX = (int) b.getX() - 550;
        pointerLocationIntY = (int) b.getY() - 200;
        if (type.equals("MENU")) {
            if (pointerLocationIntX >= x && pointerLocationIntX <= x + this.width && pointerLocationIntY >= y + height && pointerLocationIntY <= (y) + (height * 2)) {
                if (this.mousePressed = false) {
                    buttonImgState = 3;
                } else {
                    buttonImgState = 2;
                }

            } else {
                buttonImgState = 1;
            }
        } else {
            if ((pointerLocationIntX >= x && pointerLocationIntX <= x + this.width && pointerLocationIntY >= y - this.height / 2 + this.height && pointerLocationIntY <= (y) + (this.height * 2) - (height / 2))) {
                if (this.mousePressed = false) {
                    buttonImgState = 3;
                } else {
                    buttonImgState = 2;
                }

            } else {
                buttonImgState = 1;
            }
        }

        if (buttonImgState == 1) {
            g.drawImage(resting, (int) x, (int) y, null);
        }
        if (buttonImgState == 2) {
            g.drawImage(hovering, (int) x, (int) y, null);
        }
        if (buttonImgState == 3) {
            g.drawImage(clicked, (int) x, (int) y, null);
        }

    }

    //(pointerLocationIntX >= x && pointerLocationIntX <= x + this.width && pointerLocationIntY >= y-this.height/2 + this.height && pointerLocationIntY <= (y) + (this.height*2) - (height/2)
    /////////////////////////////////////////
    /////////////////////////////////////////
    /////////////////////////////////////////
    public void setMousePressed(boolean b) {
        this.mousePressed = b;
    }

    public boolean getMousePressed() {
        return mousePressed;
    }

    public BufferedImage getCurrentImg() {
        return this.currentImg;
    }

    public void setCurrentImg(int buttonImgState) {

        this.buttonImgState = buttonImgState;
    }

    public int getButtonImgState() {
        return this.buttonImgState;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x + (int) (width / 2) - (int) (width / 2 / 2), (int) y + (int) (height / 2), (int) width / 2, (int) height / 2);
    }

    public Rectangle getBoundsTop() {
        return new Rectangle((int) x + (int) (width / 2) - (int) (width / 2 / 2), (int) y, (int) width / 2, (int) height / 2);
    }

    public Rectangle getBoundsRight() {
        return new Rectangle((int) x + (int) width - 5, (int) y + 5, (int) 5, (int) height - 10);
    }

    public Rectangle getBoundsLeft() {
        return new Rectangle((int) x, (int) y + 5, (int) 5, (int) height - 10);
    }
}
