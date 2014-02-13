package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;

public class Rainbow extends Sprite {
	public static Bitmap globalBitmap;
	
	public Rainbow(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.rainbow));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth()/4;
		this.height = this.bitmap.getHeight()/3;
		this.y = context.getResources().getDisplayMetrics().heightPixels / 2;
		this.colNr = 4;
	}
}
