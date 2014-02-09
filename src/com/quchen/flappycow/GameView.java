package com.quchen.flappycow;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{
	
	public static final long UPDATE_INTERVAL = 30;
	
	Game game;
	private Thread t;
	private SurfaceHolder holder;
	volatile private boolean shouldRun = true;
	
	Cow cow;
	private Background bg;
	private Frontground fg;
	private List<Obstacle> obstacles = new ArrayList<Obstacle>();

	public GameView(Context context) {
		super(context);
		this.game = (Game) context;
		holder = getHolder();
		cow = new Cow(this, context);
		bg = new Background(this, context);
		fg = new Frontground(this, context);
	}
	
	@SuppressLint("WrongCall")
	public void run() {
		while(shouldRun){

			if(!holder.getSurface().isValid()){
				continue;
			}

			checkPasses();

			checkOutOfRange();
		
			checkCollision();

			createNew();
		
			move();

			// draw
			Canvas c = holder.lockCanvas();
			onDraw(c);

			holder.unlockCanvasAndPost(c);

			// sleep
			try {
				Thread.sleep(UPDATE_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isRunning(){
		return shouldRun;
	}
	
	public void pause(){
		// Timer pausieren
		
		shouldRun = false;
		while(t != null){
			try {
				t.join();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		t = null;
	}
	
	public void resume(){
		shouldRun = false;
		while(t != null){
			try {
				t.join();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		shouldRun = true;
		t = new Thread(this);
		t.start();
		
		// Timer fortsetzen
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		bg.draw(canvas);
		
		for(Obstacle r : obstacles){
			r.draw(canvas);
		}
		
		cow.draw(canvas);
		
		fg.draw(canvas);
		
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(getScoreTextMetrics());
		canvas.drawText("Score: " + game.points, getScoreTextMetrics(), getScoreTextMetrics(), paint);
	}
	
	private void checkPasses(){
		for(Obstacle o : obstacles){
			if(o.isPassed()){
				o.onPass();
			}
		}
	}
	
	/**
	 * Checks whether sprites are out of range and deletes them
	 */
	private void checkOutOfRange(){
		for(int i=0; i<obstacles.size(); i++){
			if(this.obstacles.get(i).isOutOfRange()){
				this.obstacles.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Checks collisions and performs the action (dmg, heal)
	 */
	private void checkCollision(){
		for(Obstacle r : obstacles){
			if(r.isColliding(cow)){
				r.onCollision();
				this.shouldRun = false;
				gameOver();
			}
		}
	}
	
	/**
	 * Can create new Gameobjects
	 */
	private void createNew(){
		if(obstacles.size() < 1){
			obstacles.add(new Obstacle(this, game));
		}
	}
	
	/**
	 * Update sprite movements
	 */
	private void move(){
		for(Obstacle o : obstacles){
			o.setSpeedX(-getSpeedX());
			o.move();
		}
		
		bg.setSpeedX(-getSpeedX()/2);
		bg.move();
		
		fg.setSpeedX(-getSpeedX()*3/2);
		fg.move();
		
		cow.move();
	}
	
	public void onTouch(){
		this.cow.onTab();
	}
	
	public int getSpeedX(){
		// 16 @ 720x1280 px
		return this.getHeight() / 80
				+ (int) (this.getHeight() / 1000f * (game.points / 3));
	}
	
	public void gameOver(){
		game.gameOver();
	}
	
	public int getScoreTextMetrics(){
		// 64 @ 720x1280 px
		return this.getHeight() / 20;
	}

}
