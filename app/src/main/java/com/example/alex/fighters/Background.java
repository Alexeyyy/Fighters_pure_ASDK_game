package com.example.alex.fighters;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Alex on 13.09.2015.
 */
public class Background {
    private Bitmap bgImage;
    private int x; //координата по Ox
    private int y;  //координата по Oy
    private int dx; //скорость смещения

    public Background(Bitmap bg) {
        this.bgImage = bg;
        this.dx = Player.SPEED;
    }

    //Расчет
    public void update() {
        this.x += dx;
        if(x < -GamePanel.WIDTH) {
            x = 0;
        }
    }

    //Отрисовка background
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bgImage, x, y, null);
        if(x < 0) {
            canvas.drawBitmap(bgImage, x + GamePanel.WIDTH, y, null);
        }
    }

}
