/**
 * The Game
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

package com.quchen.flappycow;

// Remove the imports below, if you don't want ads
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.GamesClient;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.sec.android.ad.*;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Game extends BaseGameActivity{
	/** Name of the SharedPreference that saves the medals */
	public static final String coin_save = "coin_save";
	
	/** Key that saves the medal */
	public static final String coin_key = "coin_key";
	
	private int WHICH_AD = 0; 	// 0 = google, 1 = samsung
	
	/** Will play things like mooing */
	public static SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
	
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
		setLayouts();
		initMusicPlayer();
		loadCoins();
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
	
	/**
	 * Creates the layout containing a layout for ads and the GameView
	 */
	private void setLayouts(){
		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		// Remove the lines below, if you don't want ads
		View ad;
		if(WHICH_AD == 0){
			ad = createAdMob();
		}else{
			ad = createAdHub();
		}
		mainLayout.addView(ad);
		// Remove the lines above, if you don't want ads
		
		mainLayout.addView(view);

		setContentView(mainLayout);
	}
	
	// Remove the method below, if you don't want ads
	/** Samsung AdHub */
	private View createAdHub(){
		AdHubView adhubView = new AdHubView(this);
		adhubView.init(this, getResources().getString(R.string.inventory_id), com.sec.android.ad.info.AdSize.BANNER_320x50);
		adhubView.startAd();
		
		return adhubView;
	}
	
	// Remove the method below, if you don't want ads
	/** Google AdMob */
	private AdView createAdMob(){
		AdView adView = new AdView(this);
		adView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
		adView.setAdSize(AdSize.BANNER);
		
		adView.loadAd(new AdRequest.Builder().build());
		return adView;
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
		view.resume();
		if(musicShouldPlay){
			musicPlayer.start();
		}
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS){
			Toast.makeText(this, "Please check your Google Services", Toast.LENGTH_LONG).show();
//			finish();
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
		handler.sendMessage(Message.obtain(handler,0));
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
					handler.sendMessage(Message.obtain(handler,1,R.string.toast_achievement_bronze, 0));
				}
			}
			
			if(accomplishmentBox.points >= AccomplishmentBox.SILVER_POINTS){
				if(!accomplishmentBox.achievement_silver){
					accomplishmentBox.achievement_silver = true;
					if(getGamesClient().isConnected()){
						getGamesClient().unlockAchievement(getResources().getString(R.string.achievement_silver));
					}else{
						handler.sendMessage(Message.obtain(handler,1,R.string.toast_achievement_silver, 0));
					}
				}
				
				if(accomplishmentBox.points >= AccomplishmentBox.GOLD_POINTS){
					if(!accomplishmentBox.achievement_gold){
						accomplishmentBox.achievement_gold = true;
						if(getGamesClient().isConnected()){
							getGamesClient().unlockAchievement(getResources().getString(R.string.achievement_gold));
						}else{
							handler.sendMessage(Message.obtain(handler,1,R.string.toast_achievement_gold, 0));
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
		private Game game;
		
		public MyHandler(Game game){
			this.game = game;
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 0:
					game.gameOverDialog.init();
					game.gameOverDialog.show();
					break;
				case 1:
					Toast.makeText(game, msg.arg1, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onSignInFailed() {}

	@Override
	public void onSignInSucceeded() {}
}
