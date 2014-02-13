/**
 * A spider with web
 */
package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;

public class Spider extends Sprite {
	
	public static Bitmap globalBitmap;

	public Spider(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.spider_full));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
	}
	
	public void init(int x, int y){
		this.x = x;
		this.y = y;
	}

}
