/**
 * Manages the bitmap at the front: trees and ground
 */

package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Frontground extends Background {

	public static Bitmap globalBitmap;
	
	public Frontground(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.fg);
			globalBitmap = bd.getBitmap();
		}
		this.bitmap = globalBitmap;
	}
}
