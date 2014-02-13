package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;

public class Toast extends PowerUp {
	
	public static Bitmap globalBitmap;

	public Toast(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.toast));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
	}

	@Override
	public void onCollision() {
		super.onCollision();
		view.changeToNyanCat();
	}
	
	
}
