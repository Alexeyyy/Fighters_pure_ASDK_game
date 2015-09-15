package com.example.alex.fighters;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Alex on 13.09.2015.
 */
public class Bullet extends GameObject {
    private int damage;
    private int speed;
    private int direction;
    private Paint paint;
    private Configuration.ObjectType targetType;

    public Bullet(int dir, int x, int y, int s, int dmg, int r, int color, Configuration.ObjectType target) {
        dy = 0;
        direction = dir;
        speed = s;
        damage = dmg;
        this.x = x;
        this.y = y;
        width = height = r;
        type = Configuration.ObjectType.Missile;
        targetType = target;

        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawCircle(x, y, width, paint);
        }
        catch (Exception e) { }
    }

    public void update() {
        x -= (speed * direction);
    }

    public boolean checkHit(GameObject obj) {
        if(obj.type == targetType)
            return obj.getRect().intersect(this.getRect());
        return false;
    }

    public int getDamage() {
        return damage;
    }
}
