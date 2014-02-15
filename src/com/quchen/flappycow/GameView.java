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
	
	public static final long UPDATE_INTERVAL = 30;	// milliseconds the thread sleeps after drawing

	Game game;
	private Thread t;
	private SurfaceHolder holder;
	volatile private boolean shouldRun = true;
	private boolean showedTutorial = false;
	
	PlayableCharacter player;
	private Background bg;
	private Frontground fg;
	private List<Obstacle> obstacles = new ArrayList<Obstacle>();
	private List<PowerUp> powerUps = new ArrayList<PowerUp>();
	
	private PauseButton pauseButton;
	private Tutorial tutorial;

	public GameView(Context context) {
		super(context);
		this.game = (Game) context;

		holder = getHolder();
		player = new Cow(this, context);
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
				this.player.onTab();
			}
		}
		
		return true;
	}
	
	public void run() {
		//draw at least once
		draw();
		
		while(shouldRun || !showedTutorial){
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
	
//				Log.i("FlappyCow", "GameViewRun: " + (System.currentTimeMillis() - t1));
//				t1 = System.currentTimeMillis();
				
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
		
		player.move();
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
		for(PowerUp p : powerUps){
			p.draw(canvas);
		}
		player.draw(canvas);
		fg.draw(canvas);
		pauseButton.draw(canvas);
		
		// Score Text
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(getScoreTextMetrics());
		canvas.drawText(game.getResources().getString(R.string.onscreen_score_text) + " " + game.points, getScoreTextMetrics(), getScoreTextMetrics(), paint);
	}
	
	private void drawTutorial(Canvas canvas) {
		tutorial.move();
		tutorial.draw(canvas);
	}
	
	/**
	 * Let the cow fall to the ground
	 */
	private void playerDeadFall(){
		player.dead();
		do{
			player.move();
			draw();
		}while(!player.isTouchingGround());
	}
	
	private void checkPasses(){
		for(Obstacle o : obstacles){
			if(o.isPassed()){
				o.onPass();
				createPowerUp();
			}
		}
	}
	
	/**
	 * Creates a toast with a certain chance
	 */
	private void createPowerUp(){
		// Toast
		if(game.points >= 40 && powerUps.size() < 1 && !(player instanceof NyanCat)){
			// If no powerUp is present and you have more than / equal 40 points
			if(Math.random()*100 < 33){	// 33% chance
				powerUps.add(new Toast(this, game));
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
		for(int i=0; i<powerUps.size(); i++){
			if(this.powerUps.get(i).isOutOfRange()){
				this.powerUps.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Checks collisions and performs the action
	 */
	private void checkCollision(){
		for(Obstacle o : obstacles){
			if(o.isColliding(player)){
				o.onCollision();
				gameOver();
			}
		}
		for(int i=0; i<powerUps.size(); i++){
			if(this.powerUps.get(i).isColliding(player)){
				this.powerUps.get(i).onCollision();
				this.powerUps.remove(i);
				i--;
			}
		}
		if(player.isTouchingEdge()){
			gameOver();
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
		for(PowerUp p : powerUps){
			p.move();
		}
		
		bg.setSpeedX(-getSpeedX()/2);
		bg.move();
		
		fg.setSpeedX(-getSpeedX()*4/3);
		fg.move();
		
		pauseButton.move();
		
		player.move();
	}
	
	public void changeToNyanCat(){
		PlayableCharacter tmp = this.player;
		this.player = new NyanCat(this, game);
		this.player.setX(tmp.x);
		this.player.setY(tmp.y);
		
		game.musicShouldPlay = true;
		Game.musicPlayer.start();
	}
	
	/**
	 * return the speed of the obstacles/cow
	 */
	public int getSpeedX(){
		// 16 @ 720x1280 px
		int speedDefault = this.getWidth() / 45;
		// 1,2 every 4 points @ 720x1280 px
		int speedIncrease = (int) (this.getWidth() / 600f * (game.points / 4));
		
		int speed = speedDefault + speedIncrease;
		
		if(speed > 2*speedDefault){
			return 2*speedDefault;
		}else{
			return speed;
		}
	}
	
	public void gameOver(){
		this.shouldRun = false;
		playerDeadFall();
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
