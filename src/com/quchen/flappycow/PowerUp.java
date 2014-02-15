package com.quchen.flappycow;

import android.content.Context;

public abstract class PowerUp extends Sprite {
	public PowerUp(GameView view, Context context) {
		super(view, context);
		init();
	}
	
	private void init(){
		this.x = view.getWidth() * 4/5;
		this.y = 0 - this.height;
		this.speedX = - view.getSpeedX();
		this.speedY = view.getSpeedX();
	}
}
