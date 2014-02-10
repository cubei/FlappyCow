/**
 * An obstacle: spider + logHead
 */

package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Canvas;

public class Obstacle extends Sprite{
	private Spider spider;
	private LogHead logHead;
	private boolean isPassed = false;

	public Obstacle(GameView view, Context context) {
		super(view, context);
		spider = new Spider(view, context);
		logHead = new LogHead(view, context);
		
		initPos();
	}
	
	private void initPos(){
		int height = context.getResources().getDisplayMetrics().heightPixels;
		int gab = height / 4 - view.getSpeedX();
		if(gab < height / 5){
			gab = height / 5;
		}
		int random = (int) (Math.random() * height / 2);
		int y1 = (height / 10) + random - spider.height;
		int y2 = (height / 10) + random + gab;
		
		spider.init(context.getResources().getDisplayMetrics().widthPixels, y1);
		logHead.init(context.getResources().getDisplayMetrics().widthPixels, y2);
	}

	@Override
	public void draw(Canvas canvas) {
		spider.draw(canvas);
		logHead.draw(canvas);
	}

	@Override
	public boolean isOutOfRange() {
		return spider.isOutOfRange() && logHead.isOutOfRange();
	}

	@Override
	public boolean isColliding(Sprite sprite) {
		return spider.isColliding(sprite) || logHead.isColliding(sprite);
	}

	@Override
	public void move() {
		spider.move();
		logHead.move();
	}

	@Override
	public void setSpeedX(float speedX) {
		spider.setSpeedX(speedX);
		logHead.setSpeedX(speedX);
	}
	
	@Override
	public boolean isPassed(){
		return spider.isPassed() && logHead.isPassed();
	}
	
	@Override
	public void onPass(){
		if(!isPassed){
			isPassed = true;
			view.game.obsticalPassed();
		}
	}

}
