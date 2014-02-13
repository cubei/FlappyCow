package com.quchen.flappycow;

import android.content.Context;

public class PlayableCharacter extends Sprite {
	
	public PlayableCharacter(GameView view, Context context) {
		super(view, context);
	}
	
	@Override
	public void move(){
		this.x = this.view.getWidth() / 6;
		
		if(speedY < 0){
			speedY = speedY * 2 / 3 + getSpeedTimeDecrease() / 2;
		}else{
			this.speedY += getSpeedTimeDecrease();
		}
		
		if(this.speedY > getMaxSpeed()){
			this.speedY = getMaxSpeed();
		}
		
		if(this.y + this.height > this.view.getHeight() - this.view.getHeight() * Frontground.GROUND_HEIGHT) {
			// Touching ground
			view.gameOver();
		}
		if(this.y < 0){
			// Touching sky
			view.gameOver();
		}
		
		super.move();
	}
	
	public void onTab(){
		this.speedY = getTabSpeed();
		this.y += getPosTabIncrease();
	}
	
	protected float getMaxSpeed(){
		// 25 @ 720x1280 px
		return view.getHeight() / 51.2f;
	}
	
	protected float getSpeedTimeDecrease(){
		// 4 @ 720x1280 px
		return view.getHeight() / 320;
	}
	
	protected float getTabSpeed(){
		// -80 @ 720x1280 px
		return - view.getHeight() / 16f;
	}
	
	protected int getPosTabIncrease(){
		// -12 @ 720x1280 px
		return - view.getHeight() / 100;
	}
}
