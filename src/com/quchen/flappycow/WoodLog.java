/**
 * A shopped wodden log
 */

package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;

public class WoodLog extends Sprite {

	public static Bitmap globalBitmap;

	public WoodLog(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.log_full));
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
