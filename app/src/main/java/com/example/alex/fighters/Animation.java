package com.example.alex.fighters;

import android.graphics.Bitmap;

/**
 * Created by Alex on 13.09.2015.
 */
public class Animation {
    private Bitmap[] frames; //кадры анимации
    private int currentFrame; //текущий кадр
    private long startTime; //время начало анимации
    private long delay; //задержка между кадрами анимации
    private boolean playedOnce; //одиночная/постоянная анимации

    public void Animation() { }

    public void setFrames(Bitmap[] frames) {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
    }

    public void setDelay(long d) {
        this.delay = d;
    }

    public void setFrame(int i) {
        this.currentFrame = i;
    }

    public int getFrame() {
        return this.currentFrame;
    }

    public Bitmap getImage() {
        return frames[currentFrame];
    }

    public boolean playedOnce() {
        return playedOnce;
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime)/1000000;
        if(elapsed > delay) {
            currentFrame++;
            startTime = System.nanoTime();
        }
        if(currentFrame == frames.length) {
            currentFrame = 0;
            playedOnce = true;
        }
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
    public int getFramesCount() {
        return frames.length;
    }
}
