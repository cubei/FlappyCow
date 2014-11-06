/**
 * The Game
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

package com.quchen.flappycow;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.GamesClient;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.android.gms.ads.*;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class Game extends BaseGameActivity{
	/** Name of the SharedPreference that saves the medals */
	public static final String coin_save = "coin_save";
	
	/** Key that saves the medal */
	public static final String coin_key = "coin_key";
	
	/** Will play things like mooing */
	public static SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	
	private static final int GAMES_PER_AD = 3;
	private static int gameOverCounter = 1;
	private InterstitialAd interstitial;
	
	/**
	 * Will play songs like:
	 * nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan
	 * nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan
	 * nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan
	 * nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan nyan
	 * Does someone know the second verse ???
	 */
	public static MediaPlayer musicPlayer = null;
	
	/**
	 * Whether the music should play or not
	 */
	public boolean musicShouldPlay = false;
	
	/** Time interval (ms) you have to press the backbutton twice in to exit */
	private static final long DOUBLE_BACK_TIME = 1000;
	
	/** Saves the time of the last backbutton press*/
	private long backPressed;
	
	/** To do UI things from different threads */
	public MyHandler handler;
	
	/** Hold all accomplishments */
	AccomplishmentBox accomplishmentBox;
	
	/** The view that handles all kind of stuff */
	GameView view;
	
	/** The amount of collected coins */
	int coins;
	
	/** This will increase the revive price */
	public int numberOfRevive = 1;
	
	/** The dialog displayed when the game is over*/
	GameOverDialog gameOverDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accomplishmentBox = new AccomplishmentBox();
		view = new GameView(this);
		gameOverDialog = new GameOverDialog(this);
		handler = new MyHandler(this);
		setContentView(view);
		initMusicPlayer();
		loadCoins();
		if(gameOverCounter % GAMES_PER_AD == 0) {
			setupAd();
		}
	}
	

	/**
	 * Initializes the player with the nyan cat song
	 * and sets the position to 0.
	 */
	public void initMusicPlayer(){
		if(musicPlayer == null){
			// to avoid unnecessary reinitialisation
			musicPlayer = MediaPlayer.create(this, R.raw.nyan_cat_theme);
			musicPlayer.setLooping(true);
			musicPlayer.setVolume(MainActivity.volume, MainActivity.volume);
		}
		musicPlayer.seekTo(0);	// Reset song to position 0
	}
	
	private void loadCoins(){
		SharedPreferences saves = this.getSharedPreferences(coin_save, 0);
        this.coins = saves.getInt(coin_key, 0);
	}

	/**
	 * Pauses the view and the music
	 */
	@Override
	protected void onPause() {
		view.pause();
		if(musicPlayer.isPlaying()){
			musicPlayer.pause();
		}
		super.onPause();
	}

	/**
	 * Resumes the view (but waits the view waits for a tap)
	 * and starts the music if it should be running.
	 * Also checks whether the Google Play Services are available.
	 */
	@Override
	protected void onResume() {
		view.drawOnce();
		if(musicShouldPlay){
			musicPlayer.start();
		}
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS){
			Toast.makeText(this, "Please check your Google Services", Toast.LENGTH_LONG).show();
		}
		super.onResume();
	}
	
	/**
	 * Prevent accidental exits by requiring a double press.
	 */
	@Override
	public void onBackPressed() {
		if(System.currentTimeMillis() - backPressed < DOUBLE_BACK_TIME){
			super.onBackPressed();
		}else{
			backPressed = System.currentTimeMillis();
			Toast.makeText(this, getResources().getString(R.string.on_back_press), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Sends the handler the command to show the GameOverDialog.
	 * Because it needs an UI thread.
	 */
	public void gameOver(){
		if(gameOverCounter % GAMES_PER_AD == 0) {
			handler.sendMessage(Message.obtain(handler, MyHandler.SHOW_AD));
		} else {
			handler.sendMessage(Message.obtain(handler, MyHandler.GAME_OVER_DIALOG));
		}
		
	}
	
	public void increaseCoin(){
		this.coins++;
		if(coins >= 50 && !accomplishmentBox.achievement_50_coins){
			accomplishmentBox.achievement_50_coins = true;
			if(getGamesClient().isConnected()){
				getGamesClient().unlockAchievement(getResources().getString(R.string.achievement_50_coins));
			}else{
				handler.sendMessage(Message.obtain(handler,1,R.string.toast_achievement_50_coins, 0));
			}
		}
	}

	/**
	 * What should happen, when an obstacle is passed?
	 */
	public void increasePoints(){
		accomplishmentBox.points++;
		if(accomplishmentBox.points >= AccomplishmentBox.BRONZE_POINTS){
			if(!accomplishmentBox.achievement_bronze){
				accomplishmentBox.achievement_bronze = true;
				if(getGamesClient().isConnected()){
					getGamesClient().unlockAchievement(getResources().getString(R.string.achievement_bronze));
				}else{
					handler.sendMessage(Message.obtain(handler, MyHandler.SHOW_TOAST, R.string.toast_achievement_bronze, 0));
				}
			}
			
			if(accomplishmentBox.points >= AccomplishmentBox.SILVER_POINTS){
				if(!accomplishmentBox.achievement_silver){
					accomplishmentBox.achievement_silver = true;
					if(getGamesClient().isConnected()){
						getGamesClient().unlockAchievement(getResources().getString(R.string.achievement_silver));
					}else{
						handler.sendMessage(Message.obtain(handler, MyHandler.SHOW_TOAST, R.string.toast_achievement_silver, 0));
					}
				}
				
				if(accomplishmentBox.points >= AccomplishmentBox.GOLD_POINTS){
					if(!accomplishmentBox.achievement_gold){
						accomplishmentBox.achievement_gold = true;
						if(getGamesClient().isConnected()){
							getGamesClient().unlockAchievement(getResources().getString(R.string.achievement_gold));
						}else{
							handler.sendMessage(Message.obtain(handler, MyHandler.SHOW_TOAST, R.string.toast_achievement_gold, 0));
						}
					}
				}
			}
		}
	}
	
	public GamesClient getGamesClient(){
		return this.mHelper.getGamesClient();
	}
	
	/**
	 * Shows the GameOverDialog when a message with code 0 is received.
	 */
	static class MyHandler extends Handler{
		public static final int GAME_OVER_DIALOG = 0;
		public static final int SHOW_TOAST = 1;
		public static final int SHOW_AD = 2;
		
		private Game game;
		
		public MyHandler(Game game){
			this.game = game;
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case GAME_OVER_DIALOG:
					showGameOverDialog();
					break;
				case SHOW_TOAST:
					Toast.makeText(game, msg.arg1, Toast.LENGTH_SHORT).show();
					break;
				case SHOW_AD:
					showAd();
					break;
			}
		}
		
		private void showAd() {
			if(game.interstitial == null) {
				showGameOverDialog();
			} else {
				if(game.interstitial.isLoaded()) {
					game.interstitial.show();
				} else {
					showGameOverDialog();
				}
			}
		}
		
		private void showGameOverDialog() {
			++Game.gameOverCounter;
			game.gameOverDialog.init();
			game.gameOverDialog.show();
		}
	}
	


	@Override
	public void onSignInFailed() {}

	@Override
	public void onSignInSucceeded() {}
	
	private void setupAd() {
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(getResources().getString(R.string.ad_unit_id));
		AdRequest adRequest = new AdRequest.Builder().build();
		interstitial.loadAd(adRequest);
		interstitial.setAdListener(new MyAdListener());
	}
	
	
	private class MyAdListener extends AdListener{
		public void onAdClosed () {
			handler.sendMessage(Message.obtain(handler, MyHandler.GAME_OVER_DIALOG));
		}
	}
}
