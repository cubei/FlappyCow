package com.quchen.flappycow;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Cow extends Sprite {
	
	private static int sound = -1;

	public Cow(GameView view, Context context) {
		super(view, context);
		this.bitmap = createBitmap(context.getResources().getDrawable(R.drawable.cow));
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight()/3;
		this.y = context.getResources().getDisplayMetrics().heightPixels / 2;
		
		if(sound == -1){
			sound = Game.soundPool.load(context, R.raw.cow, 1);
		}
	}
	
	private void playSound(){
		Game.soundPool.play(sound, 0.3f, 0.3f, 0, 0, 1);
	}
	
	@Override
	public void move(){
		this.x = this.view.getWidth() / 6;
		
		if(speedY < 0){
			speedY = speedY * 2 / 3 + getSpeedTimeDecrease() / 4;
		}else{
			this.speedY += getSpeedTimeDecrease();
		}
		
		if(this.speedY > getMaxSpeed()){
			this.speedY = getMaxSpeed();
		}
		if(this.speedY < getMinSpeed()){
			this.speedY = getMinSpeed();
		}
		
		if(this.y + this.height > this.view.getHeight()) {
			this.y = this.view.getHeight() - this.height;
			// Game over ?
		}
		if(this.y < 0){
			this.y = 0;
		}
		
		if(speedY > getTabSpeed() * 1/3 && speedY < getMaxSpeed() * 1/3){
			row = 0;
		}else if(speedY > 0){
			row = 1;
		}else{
			row = 2;
		}
		
		super.move();
	}
	
	public void onTab(){
		this.speedY = getTabSpeed();
		this.y += getPosTabIncrease();
		playSound();
	}
	
	private float getMinSpeed(){
		// -50 @ 720x1280 px
		return - view.getHeight() / 25.6f;
	}
	
	private float getMaxSpeed(){
		// 25 @ 720x1280 px
		return view.getHeight() / 51.2f;
	}
	
	private float getSpeedTimeDecrease(){
		// 4 @ 720x1280 px
		return view.getHeight() / 320;
	}
	
	private float getTabSpeed(){
		// -35 @ 720x1280 px
		return - view.getHeight() / 36.5f;
	}
	
	private int getPosTabIncrease(){
		// -7 @ 720x1280 px
		return - view.getHeight() / 180;
	}

}
