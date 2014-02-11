/**
 * Manages the Bitmap for the background: mountains and clouds
 */

package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Background extends Sprite {

	public static Bitmap globalBitmap;
	
	public Background(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bg);
			globalBitmap = bd.getBitmap();
		}
		this.bitmap = globalBitmap;
	}

	@Override
	public void draw(Canvas canvas) {
		double factor = (1.0 * canvas.getHeight()) / bitmap.getHeight();
		int scaledWidth = (int) (factor * bitmap.getWidth());
		if(-x > bitmap.getWidth()){
			x += bitmap.getWidth();
		}
		int endBitmap = Math.min(-x + (int) (canvas.getWidth() / factor), bitmap.getWidth());
		int endCanvas = (int) ((endBitmap + x) * factor);
		Rect src = new Rect(-x, 0, endBitmap, bitmap.getHeight());
		Rect dst = new Rect(0, 0, endCanvas, canvas.getHeight());
		canvas.drawBitmap(this.bitmap, src, dst, null);
		
		if(endBitmap == bitmap.getWidth()){
			// draw second bitmap
			src = new Rect(0, 0, (int) (canvas.getWidth() / factor), bitmap.getHeight());
			dst = new Rect(endCanvas, 0, endCanvas + canvas.getWidth(), canvas.getHeight());
			canvas.drawBitmap(this.bitmap, src, dst, null);
		}
	}
}
