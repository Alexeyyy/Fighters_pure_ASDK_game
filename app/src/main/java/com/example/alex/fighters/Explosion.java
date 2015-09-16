package com.example.alex.fighters;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Alex on 14.09.2015.
 */
public class Explosion {
    private int x;
    private int y;
    private int width;
    private int height;
    private Animation animation;
    private Bitmap sprite;
    private int dx;
    private Configuration.ObjectType objectType;

    public Explosion(int x, int y, int w, int h, Bitmap s, int numFrames, int dx) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        sprite = s;
        this.dx = dx;
        setUpAnimation(numFrames);
    }

    private void setUpAnimation(int numFrames) {
        animation = new Animation();
        int row = 0;
        Bitmap[] frames = new Bitmap[numFrames];
        for(int i = 0; i < frames.length; i++) {
            if(i % 5 == 0 && i > 0)
                row++;
            frames[i] = Bitmap.createBitmap(sprite, (i - (5 * row)) * width, row * height, width, height);
        }
        animation.setFrames(frames);
        animation.setDelay(100);
    }

    public void draw(Canvas canvas) {
        if(!animation.playedOnce()) {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }
    }
    public void update() {
        if(!animation.playedOnce())
            animation.update();
        x += dx;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFinished() {
        if(animation.getFramesCount() - 1 > animation.getCurrentFrame())
            return false;
        else
            return true;
    }

    public void setX(int dx, int dir) {
        this.x += (dx * dir);
    }

    public Bitmap getBitmap() {
        return this.sprite;
    }

    public void setObjectType(Configuration.ObjectType type) { objectType = type; }

    public Configuration.ObjectType getObjectType() { return objectType; }
}
