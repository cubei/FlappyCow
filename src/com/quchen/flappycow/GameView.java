/**
 * GameView
 * Probably the most important class for the game
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

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
	
	/** Milliseconds the thread sleeps after drawing */
	public static final long UPDATE_INTERVAL = 30;

	/** The thread that checks, moves and draws */
	private Thread thread;
	
	/** The surfaceholder needed for the canvas drawing */
	private SurfaceHolder holder;
	
	/** Whether the thread should run or not */
	volatile private boolean shouldRun = true;
	
	/** Whether the tutorial was already shown */
	private boolean showedTutorial = false;
	
	private Game game;
	private PlayableCharacter player;
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
		player = new Cow(this, game);
		bg = new Background(this, game);
		fg = new Frontground(this, game);
		pauseButton = new PauseButton(this, game);
		tutorial = new Tutorial(this, game);
		
		setOnTouchListener(this);
	}
	
	/**
	 * Manages the touchevents
	 */
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
				this.player.onTap();
			}
		}
		
		return true;
	}
	
	/**
	 * The thread runs this method
	 */
	public void run() {
		draw();		//draw at least once
		
		while(shouldRun || !showedTutorial){
			if(!showedTutorial){
				showTutorial();
			}else{
				// check
				checkPasses();
				checkOutOfRange();
				checkCollision();
				createObstacle();
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
	
	/**
	 * Joins the thread
	 */
	public void pause(){
		shouldRun = false;
		while(thread != null){
			try {
				thread.join();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		thread = null;
	}
	
	/**
	 * Activates the thread.
	 * But shouldRun will be false.
	 * This means the canvas will be drawn once.
	 * If this is the first start of a game, the tutorial will be drawn.
	 */
	public void resume(){
		pause();	// make sure the old thread isn't running
		thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Start the thread and let it run.
	 */
	public void resumeAndKeepRunning(){
		pause();	// make sure the old thread isn't running
		shouldRun = true;
		thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Draws all gameobjects on the surface
	 */
	private void draw() {
		while(!holder.getSurface().isValid()){/*wait*/}
		Canvas canvas = holder.lockCanvas();
		drawCanvas(canvas);
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * Draws all gameobjects on the canvas
	 * @param canvas
	 */
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
		canvas.drawText(game.getResources().getString(R.string.onscreen_score_text) + " " + game.accomplishmentBox.points
						+ " / " + game.getResources().getString(R.string.onscreen_coin_text) + " " + game.coins,
						getScoreTextMetrics(), getScoreTextMetrics(), paint);
	}
	
	/**
	 * Draws the tutorial on the canvas
	 * @param canvas
	 */
	private void drawTutorial(Canvas canvas) {
		tutorial.move();
		tutorial.draw(canvas);
	}
	
	/**
	 * Let the player fall to the ground
	 */
	private void playerDeadFall(){
		player.dead();
		do{
			player.move();
			draw();
			// sleep
			try {
				Thread.sleep(UPDATE_INTERVAL/4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(!player.isTouchingGround());
	}
	
	/**
	 * Draws everything normal,
	 * except the player will only be drawn, when the parameter is true
	 * @param drawPlayer
	 */
	private void drawBlinking(boolean drawPlayer){
		while(!holder.getSurface().isValid()){/*wait*/}
		Canvas canvas = holder.lockCanvas();
		bg.draw(canvas);
		for(Obstacle r : obstacles){
			r.draw(canvas);
		}
		for(PowerUp p : powerUps){
			p.draw(canvas);
		}
		if(drawPlayer){
			player.draw(canvas);
		}
		fg.draw(canvas);
		pauseButton.draw(canvas);
		
		// Score Text
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(getScoreTextMetrics());
		canvas.drawText(game.getResources().getString(R.string.onscreen_score_text) + " " + game.accomplishmentBox.points
						+ " / " + game.getResources().getString(R.string.onscreen_coin_text) + " " + game.coins,
						getScoreTextMetrics(), getScoreTextMetrics(), paint);
		holder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * Checks whether a obstacle is passed.
	 */
	private void checkPasses(){
		for(Obstacle o : obstacles){
			if(o.isPassed()){
				if(!o.isAlreadyPassed){
					o.onPass();
					createPowerUp();
				}
			}
		}
	}
	
	/**
	 * Creates a toast with a certain chance
	 */
	private void createPowerUp(){
		if((powerUps.size() < 1) && (Math.random()*100 < 20)){
			// If no powerUp is present and 20% chance
			powerUps.add(new Coin(this, game));
		}
		// Toast
		if(game.accomplishmentBox.points >= 40 && powerUps.size() < 1 && !(player instanceof NyanCat)){
			// If no powerUp is present and you have more than / equal 40 points
			if(Math.random()*100 < 33){	// 33% chance
				powerUps.add(new Toast(this, game));
			}
		}
	}
	
	/**
	 * Checks whether the obstacles or powerUps are out of range and deletes them
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
	private void createObstacle(){
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
	
	/**
	 * Changes the player to Nyan Cat
	 */
	public void changeToNyanCat(){
		game.accomplishmentBox.achievement_toastification = true;
		
		PlayableCharacter tmp = this.player;
		this.player = new NyanCat(this, game);
		this.player.setX(tmp.x);
		this.player.setY(tmp.y);
		this.player.setSpeedX(tmp.speedX);
		this.player.setSpeedY(tmp.speedY);
		
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
		int speedIncrease = (int) (this.getWidth() / 600f * (game.accomplishmentBox.points / 4));
		
		int speed = speedDefault + speedIncrease;
		
		if(speed > 2*speedDefault){
			return 2*speedDefault;
		}else{
			return speed;
		}
	}
	
	/**
	 * Let's the player fall down dead, makes sure the runcycle stops
	 * and invokes the next method for the dialog and stuff.
	 */
	public void gameOver(){
		this.shouldRun = false;
		playerDeadFall();
		game.gameOver();
	}
	
	public void revive() {
		pause();	// make sure the old thread isn't running
		
		game.numberOfRevive++;
		
		// This needs to run another thread, so the dialog can close.
		new Thread(new Runnable() {
			@Override
			public void run() {
				setupRevive();
			}
		}).start();
	}
	
	/**
	 * Sets the player into startposition
	 * Removes obstacles.
	 * Let's the character blink a few times.
	 */
	private void setupRevive(){
		game.gameOverDialog.hide();
		player.setY(this.getHeight()/2 - player.width/2);
		player.setX(this.getWidth()/6);
		obstacles.clear();
		powerUps.clear();
		player.row = 0;
		for(int i = 0; i < 6; ++i){
			drawBlinking(i%2 == 0);
			// sleep
			try {
				Thread.sleep(UPDATE_INTERVAL*5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		resumeAndKeepRunning();
	}
	
	/**
	 * A value for the position and size of the onScreen score Text
	 */
	public int getScoreTextMetrics(){
		// 64 @ 720x1280 px
		return this.getHeight() / 20;
	}
	
	public PlayableCharacter getPlayer(){
		return this.player;
	}
	
	public Game getGame(){
		return this.game;
	}

}
