/**
 * A spider
 * 
 * instead of using a spider and multiple webs,
 * it may would be better to use just one long bitmap
 */
package com.quchen.flappycow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Spider extends Sprite {
	
	public static Bitmap globalBitmap;
	private List<Spiderweb> webs = new ArrayList<Spiderweb>();

	public Spider(GameView view, Context context) {
		super(view, context);
		if(globalBitmap == null){
			globalBitmap = createBitmap(context.getResources().getDrawable(R.drawable.spider));
		}
		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
	}
	
	public void init(int x, int y){
		this.x = x;
		this.y = y;
		
		Spiderweb web = new Spiderweb(view, context);
		web.setX(x);
		web.setY(y - web.height);
		webs.add(web);
		
		// until the web reaches the sky
		while(webs.get(webs.size()-1).getY() > 0){
			web = new Spiderweb(view, context);
			web.setX(x);
			web.setY(webs.get(webs.size()-1).getY() - web.height);
			webs.add(web);
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		for(Spiderweb w : webs){
			w.draw(canvas);
		}
	}


	@Override
	public boolean isColliding(Sprite sprite) {
		if(super.isColliding(sprite)){
			return true;
		}else{
			for(Spiderweb w : webs){
				if(w.isColliding(sprite)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void move() {
		super.move();
		for(Spiderweb w : webs){
			w.move();
		}
	}
	
	@Override
	public void setSpeedX(float speedX) {
		super.setSpeedX(speedX);
		for(Spiderweb w : webs){
			w.setSpeedX(speedX);
		}
	}

}
