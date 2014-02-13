package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class NyanCat extends PlayableCharacter {
	
	public static Bitmap globalBitmap;
	private Rainbow rainbow;
	
	public NyanCat(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.nyan_cat));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
		this.y = context.getResources().getDisplayMetrics().heightPixels / 2;
		
		this.rainbow = new Rainbow(view, context);
	}
	
	@Override
	public void move(){
		super.move();
		
		// move rainbow
		rainbow.y = this.y;
		rainbow.x = this.x - rainbow.width;
		rainbow.move();
		
		// manage frames of the rainbow
		if(speedY > getTabSpeed() / 3 && speedY < getMaxSpeed() * 1/3){
			rainbow.row = 0;
		}else if(speedY > 0){
			rainbow.row = 1;
		}else{
			rainbow.row = 2;
		}
	}
	
	@Override
	public void onTab(){
		super.onTab();
		rainbow.col = 0;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		rainbow.draw(canvas);
	}

}
