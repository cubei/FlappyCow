package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;

public class Spiderweb extends Sprite {
	
	public static Bitmap globalBitmap;

	public Spiderweb(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.spiderweb));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
	}

}
