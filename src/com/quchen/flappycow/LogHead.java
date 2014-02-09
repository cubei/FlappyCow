package com.quchen.flappycow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class LogHead extends Sprite {

	public static Bitmap globalBitmap;
	private List<Log> logs = new ArrayList<Log>();

	public LogHead(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.loghead));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
	}
	
	public void init(int x, int y){
		this.x = x;
		this.y = y;
		
		Log log = new Log(view, context);
		log.setX(x);
		log.setY(y + height);
		logs.add(log);
		
		// until the logs reach the ground
		while(logs.get(logs.size()-1).getY() + logs.get(logs.size()-1).height < view.getHeight()){
			log = new Log(view, context);
			log.setX(x);
			log.setY(logs.get(logs.size()-1).getY() + logs.get(logs.size()-1).height);
			logs.add(log);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		for(Log l : logs){
			l.draw(canvas);
		}
	}

	@Override
	public boolean isColliding(Sprite sprite) {
		if(super.isColliding(sprite)){
			return true;
		}else{
			for(Log l : logs){
				if(l.isColliding(sprite)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void move() {
		super.move();
		for(Log l : logs){
			l.move();
		}
	}
	
	@Override
	public void setSpeedX(float speedX) {
		super.setSpeedX(speedX);
		for(Log l : logs){
			l.setSpeedX(speedX);
		}
	}

}
