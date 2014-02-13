package com.quchen.flappycow;

import android.content.Context;
<<<<<<< HEAD
import android.graphics.Bitmap;
=======
>>>>>>> 869acbc96e2369968204b407a58da2c0c159e4d9

public class Cow extends PlayableCharacter {
	
	public static Bitmap globalBitmap;
	private static int sound = -1;

	public Cow(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.cow));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight()/3;
		this.y = context.getResources().getDisplayMetrics().heightPixels / 2;
		
		if(sound == -1){
			sound = Game.soundPool.load(context, R.raw.cow, 1);
		}
	}
	
	private void playSound(){
		Game.soundPool.play(sound, MainActivity.volume, MainActivity.volume, 0, 0, 1);
	}

	@Override
	public void onTab(){
		super.onTab();
		playSound();
	}
	
	@Override
	public void move(){
		super.move();
		
		// manage frames
		if(speedY > getTabSpeed() / 3 && speedY < getMaxSpeed() * 1/3){
			row = 0;
		}else if(speedY > 0){
			row = 1;
		}else{
			row = 2;
		}
	}
}
