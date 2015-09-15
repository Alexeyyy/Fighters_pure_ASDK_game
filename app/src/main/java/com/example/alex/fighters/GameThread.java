package com.example.alex.fighters;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Alex on 12.09.2015.
 */
public class GameThread extends Thread {
    private int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean executing;
    private int i = 0;

    public static Canvas canvas; //делаем его доступным для GamePanel

    public GameThread(SurfaceHolder sh, GamePanel gp) {
        this.gamePanel = gp;
        this.surfaceHolder = sh;
    }

    @Override
    public void run() {
        long startTime, timeMillis, waitTime,
        totalTime = 0, target = 1000/FPS; //"шаг" игрового цикла
        int frameCount = 0;

        while(executing) {
            startTime = System.nanoTime();
            canvas = null;

            //блокировать Canvas для его редактирования
            //синхронизация производится для рисования только одним потоком
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            }
            catch (Exception e) { }
            finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch (Exception e) { }
                }
            }

            timeMillis = (System.nanoTime() - startTime)/1000000; //определение количества времени для одного update canvas
            waitTime = target - timeMillis; //сколько времени нужно подождать перед след. итерацией цикла

            try {
                this.sleep(waitTime);
            }
            catch(Exception e) { }

            totalTime = System.nanoTime() - startTime;
            frameCount++;

            if(frameCount == FPS) {
                averageFPS = 1000/((totalTime/frameCount)/1000000) + i;
                i++;
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    public void setExecuting(boolean flag) {
        this.executing = flag;
    }

    public double getFPS() {
        return this.averageFPS;
    }
}
