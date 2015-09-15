package com.example.alex.fighters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.ViewDebug;

import java.util.ArrayList;

/**
 * Created by Alex on 13.09.2015.
 */
public class Player extends GameObject {
    private Bitmap sprite;
    private boolean playing;
    private int score;
    private long startTime;
    private boolean lostGame = false;
    private int health = 200;

    private ArrayList<Bullet> bullets;
    private int shotSpeed = 20;
    private int shotStrength = 50;
    private long shotDelay = 500;
    private long startShotTime;
    private boolean triggerPressed = false;
    private Bullet b;

    //Взрыв
    private Explosion explosion;

    //Общие параметры игрока
    public static int SPEED = -4;

    public Player(Bitmap s, int w, int h, int numFrames, long d) {
        score = 0;
        //GameObject inheritance
        x = 100; //начальная позиция по Ox
        y = GamePanel.HEIGHT / 2; //начальная позиция по Oy
        width = w;
        height = h;
        animation = new Animation();
        sprite = s;
        type = Configuration.ObjectType.Player;
        bullets = new ArrayList<Bullet>();
        setUpAnimation(numFrames, d);
        explosion = null;
    }

    private void setUpAnimation(int numFrames, long d) {
        Bitmap[] spriteFrames = new Bitmap[numFrames];

        for(int i = 0; i < spriteFrames.length; i++) {
            spriteFrames[i] = Bitmap.createBitmap(sprite, width * i, 0, this.width, this.height);
        }
        animation.setFrames(spriteFrames);
        animation.setDelay(d);
        startTime = System.nanoTime();
    }

    public void update() {
        //long elapsed = (System.nanoTime() - startTime) / 1000000; //конвертируем в миллисекунды
        animation.update();
        //уперлись в небо
        if(y <= 10)
            y = 10;
        //разбились --> ставим playing в false
        if(y >= GamePanel.HEIGHT - (height - 10)) {
            lostGame = true;
            playing = false;
            health = 0;
        }
        //Вверх
        if(GameActivity.UP)
            dy -= 2;
        //Вниз
        else
            dy += 2;

        //Плавный полет
        if(GameActivity.PLAIN)
            dy = 1; //идеально плавного полета не бывает (постоянно клоним вниз)

        y += dy;

        //Установки max и min
        if(dy > 5)
            dy = 5;
        if(dy < -5)
            dy = -5;

        //Стрельба
        for(int i = 0; i < bullets.size(); i++) {
            b = bullets.get(i);
            b.update();
            if(b.getX() > GamePanel.WIDTH + 100) {
                bullets.remove(i);
            }
            //если есть хоть один противник начинаем проверять столкновение пули и самолета противника
            if(GamePanel.enemies.size() != 0) {
                for (Enemy e : GamePanel.enemies) {
                    if (b.checkHit(e)) {
                        e.getDamage(b.getDamage());
                        if (e.getHealth() <= 0) {
                            GamePanel.enemiesExplosions.add(GamePanel.createExplosion(e.getX(), e.getY(), e.getSpeed() * (-1)));
                            GamePanel.enemies.remove(e);
                        }
                        bullets.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public void updateExplosion() {
        explosion.update();
    }

    public void draw(Canvas canvas) {
        //Игрок
        canvas.drawBitmap(animation.getImage(), x, y, null);
        //Рисуем пули
        for (Bullet b : bullets) {
            b.draw(canvas);
        }
    }

    public void drawExplosion(Canvas canvas) {
        explosion.draw(canvas);
    }

    public boolean getPlaying() {
        return this.playing;
    }

    public void setPlaying(boolean f) {
        playing = f;
    }

    public void setScrore(int s) {
        score = 0;
    }

    public int getScore() {
        return this.score;
    }

    public boolean getLostGame() {
        return this.lostGame;
    }

    public void setLostGame(boolean f) { this.lostGame = f; }

    public void resetDY() {
        dy = 0;
    }

    public void resetPlayer() {
        score = 0;
        dy = 0;
        x = 100;
        y = GamePanel.HEIGHT/2;
        health = 200;
        lostGame = false;
        bullets.clear();
        triggerPressed = false;
    }

    public void getDamage(int dmg) {
        health -= dmg;
    }

    public int getHealth() {
        return health;
    }

    public void shot() {
        long fireTime = System.nanoTime();
        if(fireTime - startShotTime > shotDelay) {
            bullets.add(new Bullet(-1, x + width, y + height / 2, shotSpeed, shotStrength, 2, Color.RED, Configuration.ObjectType.Enemy));
        }
    }

    public void setStartShotTime() {
        startShotTime = System.nanoTime();
    }

    public void setTriggerState(boolean f) {
        triggerPressed = f;
    }

    public boolean getTriggerState() {
        return triggerPressed;
    }

    public void clearBullets() {
        bullets.clear();
    }

    public void setExplosion(Explosion e) {
        explosion = e;
    }
    public Explosion getExplosion() {
        return explosion;
    }
}
