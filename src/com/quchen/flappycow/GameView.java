package com.quchen.flappycow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends SurfaceView implements Runnable, OnTouchListener{
	
	public static final long UPDATE_INTERVAL = 30;
	
	Game game;
	private Thread t;
	private SurfaceHolder holder;
	volatile private boolean shouldRun = true;
	private boolean showedTutorial = false;
	
	Cow cow;
	private Background bg;
	private Frontground fg;
	private List<Obstacle> obstacles = new ArrayList<Obstacle>();
	private PauseButton pauseButton;
	private Tutorial tutorial;

	public GameView(Context context) {
		super(context);
		this.game = (Game) context;

		holder = getHolder();
		cow = new Cow(this, context);
		bg = new Background(this, context);
		fg = new Frontground(this, context);
		pauseButton = new PauseButton(this, context);
		tutorial = new Tutorial(this, context);
		
		setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(!shouldRun){
				// Start game if it's paused
				resumeAndKeepRunning();
			}
			if(pauseButton.isTouching((int) event.getX(), (int) event.getY()) && this.shouldRun){
				pause();
			}else{
				// Cow flap
				this.cow.onTab();
			}
		}
		return true;
	}
	
	public void run() {
		
		//draw at least once
		draw();
		
		while(shouldRun || !showedTutorial){
			if(!holder.getSurface().isValid()){
				continue;
			}
			
			if(!showedTutorial){
				showTutorial();
			}else{
			
				// check
				checkPasses();
				checkOutOfRange();
				checkCollision();
				createNew();
				move();
	
				draw();
	
//				Log.i("FlappyCow", "GameViewRun: " + System.currentTimeMillis());
				
				// sleep
				try {
					Thread.sleep(UPDATE_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Draw Tutorial
	 */
	public void showTutorial(){
		showedTutorial = true;
		
		cow.move();
		pauseButton.move();
		
		while(!holder.getSurface().isValid()){/*wait*/}
		
		Canvas canvas = holder.lockCanvas();
		drawCanvas(canvas);
		drawTutorial(canvas);
		holder.unlockCanvasAndPost(canvas);
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
		pause();	// make sure the old thread isn't running
		t = new Thread(this);
		t.start();
	}
	
	public void resumeAndKeepRunning(){
		pause();	// make sure the old thread isn't running
		shouldRun = true;
		t = new Thread(this);
		t.start();
	}
	
	private void draw() {
		while(!holder.getSurface().isValid()){/*wait*/}
		Canvas canvas = holder.lockCanvas();
		drawCanvas(canvas);
		holder.unlockCanvasAndPost(canvas);
	}
	
	private void drawCanvas(Canvas canvas) {
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
	
	private void drawTutorial(Canvas canvas) {
		tutorial.move();
		tutorial.draw(canvas);
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
		int speedDefault = this.getWidth() / 45;
		// 1,2 every 3 points @ 720x1280 px
		int speedIncrease = (int) (this.getWidth() / 600f * (game.points / 3));
		
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
