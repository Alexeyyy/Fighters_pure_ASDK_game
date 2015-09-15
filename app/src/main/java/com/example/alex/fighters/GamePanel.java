package com.example.alex.fighters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Alex on 12.09.2015.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    //Расчет
    private GameThread gameThread;
    private boolean newGameCreated = false;
    private boolean justEntered = true;
    private boolean disappear = true;

    //Отображение
    private Background bg;
    public static Player player;
    //Противники
    public static ArrayList<Enemy> enemies;
    public static ArrayList<Explosion> enemiesExplosions;
    private long enemyStartTime;
    private Random rand;
    private Enemy e;
    //Взрыв
    public static Explosion explosion; //копируем и взрываем где-нибудь

    //Интерфейс
    private int bestResult = 0;

    //Общие параметры
    public static int WIDTH = 856;
    public static int HEIGHT = 480;

    public GamePanel(Context context) {
        super(context);
        getHolder().addCallback(this); //для вызова событий
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    //Метод, срабатывающий при закрытии activity/приложения
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while(retry && counter < 1000) {
            counter++;
            try {
                gameThread.setExecuting(false);
                gameThread.join();
                retry = false;
                gameThread = null;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Метод инициализации игрового пространства
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.bg));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.spitfire_sprite), 90, 38, 3, 100);
        enemies = new ArrayList<Enemy>();
        enemiesExplosions = new ArrayList<Explosion>();
        enemyStartTime = System.nanoTime();
        rand = new Random();
        explosion = new Explosion(0, 0, 100, 100, BitmapFactory.decodeResource(getResources(), R.drawable.explosion), 25, 0);
        //Запуск игрового потока
        gameThread = new GameThread(getHolder(), this);
        gameThread.setExecuting(true);
        gameThread.start();
    }

    //Срабатывает при касании пальцем экрана
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_DOWN) {
            if(!player.getPlaying() && !newGameCreated) {
                resetGame();
                player.setPlaying(true);
            }
            if(!player.getTriggerState()) {
                player.setTriggerState(true);
                player.setStartShotTime();
                if (player.getPlaying()) {
                    player.shot();
                }
            }
            return true;
        }
        if(e.getAction() == MotionEvent.ACTION_UP) {
            if(player.getPlaying()) {
                player.setTriggerState(false);
            }
            return true;
        }
        return super.onTouchEvent(e);
    }

    //Обеспечивает отрисовку игрового пространства
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float scaleCoefficientX = getWidth()/(WIDTH * 1.f);
        final float scaleCoefficientY = getHeight()/(HEIGHT * 1.f);

        if(canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleCoefficientX, scaleCoefficientY);
            //задний фон
            bg.draw(canvas);
            //игрок
            if(!disappear)
                player.draw(canvas);
            //взрыв на месте игрока
            if(player.getLostGame()) {
                try {
                    player.drawExplosion(canvas);
                }
                catch (Exception e) { }
            }
            //противники
            for(Enemy e : enemies) {
                e.draw(canvas);
            }

            //взорванные противники
            for(Explosion exp : enemiesExplosions) {
                exp.draw(canvas);
            }

            //drawDebug(canvas, gameThread); //отладочная информация
            drawText(canvas);
            drawGameInterface(canvas);
            canvas.restoreToCount(savedState);
        }
    }

    //Обеспечивает расчет всего необходимого для одного frame игры
    public void update() {
        //Рассчитываем в случае запущенной игры
        if(player.getPlaying()) {
            bg.update();
            player.update();

            //создание новых самолетов противника. Пока каждые 1.5 секунд
            long enemyElapsedTime = (System.nanoTime() - enemyStartTime)/1000000;
            if(enemyElapsedTime > 1500) {
                enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.bf_sprite),
                            WIDTH - 10,
                            rand.nextInt(HEIGHT - (Configuration.BF_HEIGHT + 10)) + (Configuration.BF_HEIGHT + 10),
                            Configuration.BF_WIDTH,
                            Configuration.BF_HEIGHT,
                            Configuration.BF_FRAMECOUNT,
                            1000,
                            35,
                            10,
                            50
                        ));
                enemyStartTime = System.nanoTime();
            }
            //update для противников
            for(int i = 0; i < enemies.size(); i++) {
                e = enemies.get(i);
                e.update();
                //столкновение c игроком
                if (checkCollision(e.getRect(), player.getRect())) {
                    enemies.remove(e);
                    player.setLostGame(true);
                    player.setPlaying(false);
                    break;
                }
                //за экраном, то удаляем
                if (e.getX() <= -300) {
                    enemies.remove(e);
                }
            }

            //update для взрывов
            for(int i = 0; i < enemiesExplosions.size(); i++) {
                enemiesExplosions.get(i).update();
                if(enemiesExplosions.get(i).isFinished())
                    enemiesExplosions.remove(i);
            }
        }
        else {
            if(player.getLostGame()) {  //разбились, убили
                if (player.getExplosion() == null)
                    player.setExplosion(new Explosion(player.getX(), player.getY() - player.getHeight() / 2, 100, 100, BitmapFactory.decodeResource(getResources(), R.drawable.explosion), 25, 0));
                player.updateExplosion();
                disappear = true;
                if(player.getExplosion().isFinished())
                    player.setLostGame(false);
            }
            else { //начать новую игру
                enemies.clear();
                player.resetDY();
                player.clearBullets();
                newGameCreated = false;
                player.setExplosion(null);
            }
        }
    }

    private boolean checkCollision(Rect rect1, Rect rect2) {
        return rect1.intersect(rect2);
    }

    private void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        //Стартовый экран
        if(justEntered) {
            canvas.drawText("Новая игра. Вход", 100, HEIGHT/2, paint);
        }
        //В случае проигрыша
        if(!justEntered && !newGameCreated) {
            canvas.drawText("Проиграл. Начни новою игру", 100, HEIGHT/2, paint);
        }
    }

    private void drawGameInterface(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(("Health: " + player.getHealth()), 10, 30, paint);
    }

    //Вывод отладочной информации
    private final void drawDebug(Canvas canvas, GameThread th) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        if(th != null) {
            //canvas.drawText("FPS: " + th.getFPS(), 100, 100, paint);
            canvas.drawText(GameActivity.getAccelerometerString(), 100, 100, paint);
        }
    }
    private void resetGame() {
        justEntered = false; //никогда больше не ставится в true
        if(bestResult < player.getScore())
            bestResult = player.getScore();
        player.resetPlayer();
        newGameCreated = true;
        disappear = false;
    }

    public static Explosion createExplosion(int x, int y, int dx) {
        return new Explosion(x, y, 100, 100, explosion.getBitmap(), 25, dx);
    }
}
