package com.example.alex.fighters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Alex on 13.09.2015.
 */
public class Enemy extends GameObject {
    private Bitmap sprite;
    private int speed;
    private int health;
    private Animation animation = new Animation();
    private Random rand = new Random(); //определяет направление движения по OY

    private ArrayList<Bullet> bullets;
    private long startShooting;
    private long shootDelay = 1000;
    private int shootSpeed;
    private int shootStrength;
    private Bullet b;

    public Enemy(Bitmap s, int x, int y, int w, int h, int numFrames, long shD, int shSp, int shStr, int hlth) {
        type = Configuration.ObjectType.Enemy;
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        sprite = s;
        speed = 10;
        dy = rand.nextInt(10) - 5;
        setUpAnimation(numFrames, 100);
        bullets = new ArrayList<Bullet>();
        startShooting = System.nanoTime();
        shootDelay = shD;
        shootSpeed = shSp;
        shootStrength = shStr;
        health = hlth;
    }

    private void setUpAnimation(int numFrames, long d) {
        Bitmap[] spriteFrames = new Bitmap[numFrames];

        for(int i = 0; i < spriteFrames.length; i++) {
            spriteFrames[i] = Bitmap.createBitmap(sprite, width * i, 0, this.width, this.height);
        }
        animation.setFrames(spriteFrames);
        animation.setDelay(d);
    }

    public void update() {
        animation.update();

        //стрельба
        long fireTime = (System.nanoTime() - startShooting)/1000000;
        if(fireTime > shootDelay) {
            bullets.add(new Bullet(1, this.x, this.y + height/2, shootSpeed, shootStrength, 2, Color.BLACK, Configuration.ObjectType.Player));
            startShooting = System.nanoTime();
        }
        for(int i = 0; i < bullets.size(); i++) {
            b = bullets.get(i);
            b.update();
            if(b.getX() < -100) {
                bullets.remove(i);
            }
            if(b.checkHit(GamePanel.player)) {
                GamePanel.player.getDamage(b.getDamage());
                bullets.remove(i);
                if(GamePanel.player.getHealth() <= 0) {
                    GamePanel.player.setLostGame(true);
                    GamePanel.player.setPlaying(false);
                    break;
                }
            }
        }
        //перемещение в вертикальной плоскости
        //не позволяем упасть самолету (иначе, что за ас там сидит)
        if(y > GamePanel.HEIGHT - height - 10) {
            y = GamePanel.HEIGHT - height - 10;
            if(dy < 0)
                dy *= -1;
        }
        //не позволяем ему улететь
        if(y < height + 10) {
            y = height + 10;
            if(dy > 0)
                dy *= -1;
        }
        y -= dy;

        //Установка допустимого минимума/максимума
        if (dy > 5)
            dy = 5;
        if(dy < -5)
            dy = 5;

        //Перемещение в горизонтальной плоскости
        x -= speed;
    }

    public void draw(Canvas canvas) {
        //на всякий случай
        try {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }
        catch (Exception e) { }

        try {
            for(Bullet m : bullets) {
                m.draw(canvas);
            }
        }
        catch (Exception e) { }
    }

    public void getDamage(int dmg) {
        health -= dmg;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

}
