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
import java.util.Timer;
import java.util.TimerTask;

import com.quchen.flappycow.Game.MyHandler;
import com.quchen.flappycow.sprites.Background;
import com.quchen.flappycow.sprites.Coin;
import com.quchen.flappycow.sprites.Cow;
import com.quchen.flappycow.sprites.Frontground;
import com.quchen.flappycow.sprites.NyanCat;
import com.quchen.flappycow.sprites.Obstacle;
import com.quchen.flappycow.sprites.PauseButton;
import com.quchen.flappycow.sprites.PlayableCharacter;
import com.quchen.flappycow.sprites.PowerUp;
import com.quchen.flappycow.sprites.Toast;
import com.quchen.flappycow.sprites.Tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.android.gms.games.Games;

public class GameView extends SurfaceView{
    
    /** Milliseconds for game timer tick */
    public static final long UPDATE_INTERVAL = 50;        // = 20 FPS
    
    private Timer timer = new Timer();
    private TimerTask timerTask;
    
    /** The surfaceholder needed for the canvas drawing */
    private SurfaceHolder holder;
    
    private Game game;
    private PlayableCharacter player;
    private Background background;
    private Frontground frontground;
    private List<Obstacle> obstacles = new ArrayList<Obstacle>();
    private List<PowerUp> powerUps = new ArrayList<PowerUp>();
    
    private PauseButton pauseButton;
    volatile private boolean paused = true;
    
    private Tutorial tutorial;
    private boolean tutorialIsShown = true;

    public GameView(Context context) {
        super(context);
        this.game = (Game) context;
        setFocusable(true);

        holder = getHolder();
        player = new Cow(this, game);
        background = new Background(this, game);
        frontground = new Frontground(this, game);
        pauseButton = new PauseButton(this, game);
        tutorial = new Tutorial(this, game);
    }
    
    private void startTimer() {
        setUpTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, UPDATE_INTERVAL, UPDATE_INTERVAL);
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
    }
    
    private void setUpTimerTask() {
        stopTimer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                GameView.this.run();
            }
        };
    }
    
    @Override
    public boolean performClick() {
        return super.performClick();
        // Just to remove the stupid warning
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        if(event.getAction() == MotionEvent.ACTION_DOWN  // Only for "touchdowns"
                && !this.player.isDead()){ // No support for dead players
            if(tutorialIsShown){
                // dismiss tutorial
                tutorialIsShown = false;
                resume();
                this.player.onTap();
            }else if(paused){
                resume();
            }else if(pauseButton.isTouching((int) event.getX(), (int) event.getY()) && !this.paused){
                pause();
            }else{
                this.player.onTap();
            }
        }
        return true;
    }
    
    /**
     * content of the timertask
     */
    public void run() {
        checkPasses();
        checkOutOfRange();
        checkCollision();
        createObstacle();
        move();

        draw();
    }
    
    /**
     * Draw Tutorial
     */
    public void showTutorial(){
        player.move();
        pauseButton.move();
        
        while(!holder.getSurface().isValid()){
            /*wait*/
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        
        Canvas canvas = holder.lockCanvas();
        drawCanvas(canvas, true);
        tutorial.move();
        tutorial.draw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }
    
    public void pause(){
        stopTimer();
        paused = true;
    }
    
    public void drawOnce(){
        (new Thread(new Runnable() {
            @Override
            public void run() {
                if(tutorialIsShown){
                    showTutorial();
                } else {
                    draw();
                }
            }
        })).start();
    }
    
    public void resume(){
        paused = false;
        startTimer();
    }
    
    /**
     * Draws all gameobjects on the surface
     */
    private void draw() {
        while(!holder.getSurface().isValid()){
            /*wait*/
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        Canvas canvas = holder.lockCanvas();
        drawCanvas(canvas, true);
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * Draws everything normal,
     * except the player will only be drawn, when the parameter is true
     * @param drawPlayer
     */
    private void drawCanvas(Canvas canvas, boolean drawPlayer){
        background.draw(canvas);
        for(Obstacle r : obstacles){
            r.draw(canvas);
        }
        for(PowerUp p : powerUps){
            p.draw(canvas);
        }
        if(drawPlayer){
            player.draw(canvas);
        }
        frontground.draw(canvas);
        pauseButton.draw(canvas);
        
        // Score Text
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(getScoreTextMetrics());
        canvas.drawText(game.getResources().getString(R.string.onscreen_score_text) + " " + game.accomplishmentBox.points
                        + " / " + game.getResources().getString(R.string.onscreen_coin_text) + " " + game.coins,
                        0, getScoreTextMetrics(), paint);
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
            try { Thread.sleep(UPDATE_INTERVAL/4); } catch (InterruptedException e) { e.printStackTrace(); }
        }while(!player.isTouchingGround());
    }
    
    /**
     * Checks whether an obstacle is passed.
     */
    private void checkPasses(){
        for(Obstacle o : obstacles){
            if(o.isPassed()){
                if(!o.isAlreadyPassed){    // probably not needed
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
        // Toast
        if(game.accomplishmentBox.points >= Toast.POINTS_TO_TOAST /*&& powerUps.size() < 1*/ && !(player instanceof NyanCat)){
            // If no powerUp is present and you have more than / equal 42 points
            if(game.accomplishmentBox.points == Toast.POINTS_TO_TOAST){    // First time 100 % chance
                powerUps.add(new Toast(this, game));
            } else if(Math.random()*100 < 33){    // 33% chance
                powerUps.add(new Toast(this, game));
            }
        }
        
        if((powerUps.size() < 1) && (Math.random()*100 < 20)){
            // If no powerUp is present and 20% chance
            powerUps.add(new Coin(this, game));
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
        
        background.setSpeedX(-getSpeedX()/2);
        background.move();
        
        frontground.setSpeedX(-getSpeedX()*4/3);
        frontground.move();
        
        pauseButton.move();
        
        player.move();
    }
    
    /**
     * Changes the player to Nyan Cat
     */
    public void changeToNyanCat(){
        game.accomplishmentBox.achievement_toastification = true;
        if(game.getApiClient().isConnected()){
            Games.Achievements.unlock(game.getApiClient(), getResources().getString(R.string.achievement_toastification));
        }else{
            game.handler.sendMessage(Message.obtain(game.handler,1,R.string.toast_achievement_toastification, MyHandler.SHOW_TOAST));
        }
        
        PlayableCharacter tmp = this.player;
        this.player = new NyanCat(this, game);
        this.player.setX(tmp.getX());
        this.player.setY(tmp.getY());
        this.player.setSpeedX(tmp.getSpeedX());
        this.player.setSpeedY(tmp.getSpeedY());
        
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
        
        return Math.min(speed, 2*speedDefault);
    }
    
    /**
     * Let's the player fall down dead, makes sure the runcycle stops
     * and invokes the next method for the dialog and stuff.
     */
    public void gameOver(){
        pause();
        playerDeadFall();
        game.gameOver();
    }
    
    public void revive() {
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
        player.setY(this.getHeight()/2 - player.getWidth()/2);
        player.setX(this.getWidth()/6);
        obstacles.clear();
        powerUps.clear();
        player.revive();
        for(int i = 0; i < 6; ++i){
            while(!holder.getSurface().isValid()){/*wait*/}
            Canvas canvas = holder.lockCanvas();
            drawCanvas(canvas, i%2 == 0);
            holder.unlockCanvasAndPost(canvas);
            // sleep
            try { Thread.sleep(UPDATE_INTERVAL*6); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        resume();
    }
    
    /**
     * A value for the position and size of the onScreen score Text
     */
    public int getScoreTextMetrics(){
        return (int) (this.getHeight() / 21.0f);
        /*/ game.getResources().getDisplayMetrics().density)*/
    }
    
    public PlayableCharacter getPlayer(){
        return this.player;
    }
    
    public Game getGame(){
        return this.game;
    }

}
