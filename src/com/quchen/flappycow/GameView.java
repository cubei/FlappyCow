package com.quchen.flappycow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends SurfaceView implements Runnable, OnTouchListener{
	
	public static final long UPDATE_INTERVAL = 30;
	private long debugTime = 0;
	
	Game game;
	private Thread t;
	private SurfaceHolder holder;
	volatile private boolean shouldRun = true;
	
	Cow cow;
	private Background bg;
	private Frontground fg;
	private List<Obstacle> obstacles = new ArrayList<Obstacle>();
	private PauseButton pauseButton;

	public GameView(Context context) {
		super(context);
		this.game = (Game) context;
		holder = getHolder();
		cow = new Cow(this, context);
		bg = new Background(this, context);
		fg = new Frontground(this, context);
		pauseButton = new PauseButton(this, context);
		setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(pauseButton.isTouching((int) event.getX(), (int) event.getY())){
				// Pause
				if(this.shouldRun){
					pause();
				}else{
					resume();
				}
			}else if(shouldRun){
				// Cow flap
				this.cow.onTab();
			}
		}
		return true;
	}
	
	public void run() {
		while(shouldRun){

			if(!holder.getSurface().isValid()){
				continue;
			}

			// check
			checkPasses();
			checkOutOfRange();
			checkCollision();
			createNew();
			move();

			// draw
			Canvas c = holder.lockCanvas();
			drawCanvas(c);
			holder.unlockCanvasAndPost(c);

			// sleep
			try {
				Thread.sleep(UPDATE_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
//			Log.i("FlappyCow", "GameViewRun: " + (System.currentTimeMillis() - debugTime));
//			debugTime = System.currentTimeMillis();
		}
	}
	
	public void pause(){
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
		pause();	// make sure not 2 threads are running
		shouldRun = true;
		t = new Thread(this);
		t.start();
	}
	
	protected void drawCanvas(Canvas canvas) {
		bg.draw(canvas);
		for(Obstacle r : obstacles){
			r.draw(canvas);
		}
		cow.draw(canvas);
		fg.draw(canvas);
		pauseButton.draw(canvas);
		
		// Score Text
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
	 * Checks collisions and performs the action
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
	 * if no obstacle is present a new one is created
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
		
		fg.setSpeedX(-getSpeedX()*4/3);
		fg.move();
		
		pauseButton.move();
		
		cow.move();
	}
	
	/**
	 * return the speed of the obstacles/cow
	 */
	public int getSpeedX(){
		// 16 @ 720x1280 px
		int speedDefault = this.getHeight() / 80;
		// 1,2 every 3 points @ 720x1280 px
		int speedIncrease = (int) (this.getHeight() / 1000f * (game.points / 3));
		
		int speed = speedDefault + speedIncrease;
		
		if(speed > 2*speedDefault){
			return 2*speedDefault;
		}else{
			return speed;
		}
	}
	
	public void gameOver(){
		game.gameOver();
	}
	
	/**
	 * A value for the position and size of the onScreen score Text
	 */
	public int getScoreTextMetrics(){
		// 106 @ 720x1280 px
		return this.getHeight() / 12;
	}

}
