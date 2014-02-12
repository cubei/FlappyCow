package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;

public class Tutorial extends Sprite {
	public static Bitmap globalBitmap;

	public Tutorial(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.tutorial));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
	}

	@Override
	public void move() {
		this.x = view.getWidth() / 2 - this.width / 2;
		this.y = view.getHeight() / 2 - this.height / 2;
	}
	
	
}
