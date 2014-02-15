package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;

public class Cow extends PlayableCharacter {
	
	public static Bitmap globalBitmap;
	private static int sound = -1;

	public Cow(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.cow));
		}
		this.bitmap = globalBitmap;
		this.colNr = 8;
		this.width = this.bitmap.getWidth()/colNr;
		this.height = this.bitmap.getHeight()/4;
		this.frameTime = 3;
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
		if(row != 3){
			// not dead
			if(speedY > getTabSpeed() / 3 && speedY < getMaxSpeed() * 1/3){
				row = 0;
			}else if(speedY > 0){
				row = 1;
			}else{
				row = 2;
			}
		}
	}

	@Override
	public void dead() {
		this.row = 3;
		this.frameTime = 3;
		super.dead();
	}
}
