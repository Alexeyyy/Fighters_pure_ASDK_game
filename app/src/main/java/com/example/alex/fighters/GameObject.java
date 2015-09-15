package com.example.alex.fighters;

import android.graphics.Rect;

/**
 * Created by Alex on 13.09.2015.
 */
public class GameObject {
    protected int x; //координата x
    protected int y; //координата y
    protected int dy; //перемещение по Oy
    protected int width; //ширина объекта(спрайта)
    protected int height; //высота объекта (спрайта)
    protected Animation animation;
    protected Configuration.ObjectType type;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public Rect getRect() {
        return new Rect(this.x, this.y, this.x + this.width, this.y + this.height);
    }
}