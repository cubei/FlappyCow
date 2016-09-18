/**
 * Manages the Bitmap for the background
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

package com.quchen.flappycow.sprites;

import com.quchen.flappycow.Game;
import com.quchen.flappycow.GameView;
import com.quchen.flappycow.R;
import com.quchen.flappycow.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background extends Sprite {

    /** Static bitmap to reduce memory usage */
    public static Bitmap globalBitmap;
    
    public Background(GameView view, Game game) {
        super(view, game);
        
        if(globalBitmap == null){
            globalBitmap = Util.getDownScaledBitmapAlpha8(game, R.drawable.bg);
        }
        this.bitmap = globalBitmap;
    }

    /**
     * Draws the bitmap to the Canvas.
     * The height of the bitmap will be scaled to the height of the canvas.
     * When the bitmap is scrolled to far to the left, so it won't cover the whole screen,
     * the bitmap will be drawn another time behind the first one.
     */
    @Override
    public void draw(Canvas canvas) {
        double factor = (1.0 * canvas.getHeight()) / bitmap.getHeight();
        
        if(-x > bitmap.getWidth()){
            // The first bitmap is completely out of the screen
            x += bitmap.getWidth();
        }
        
        int endBitmap = Math.min(-x + (int) (canvas.getWidth() / factor), bitmap.getWidth());
        int endCanvas = (int) ((endBitmap + x) * factor) + 1;
        src.set(-x, 0, endBitmap, bitmap.getHeight());
        dst.set(0, 0, endCanvas, canvas.getHeight());
        canvas.drawBitmap(this.bitmap, src, dst, null);
        
        if(endBitmap == bitmap.getWidth()){
            // draw second bitmap
            src.set(0, 0, (int) (canvas.getWidth() / factor), bitmap.getHeight());
            dst.set(endCanvas, 0, endCanvas + canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(this.bitmap, src, dst, null);
        }
    }
}
