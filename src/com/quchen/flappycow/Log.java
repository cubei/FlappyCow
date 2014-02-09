package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;

public class Log extends Sprite {
	public static Bitmap globalBitmap;

	public Log(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.log));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
	}

}
