/**
 * The Game
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

package com.quchen.flappycow;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Game extends Activity{
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
	private MyHandler handler;
	
	/** The view that handles all kind of stuff */
	GameView view;
	
	/** The amount of passed obstacles */
	int points;
	
	/** The dialog displayed when the game is over*/
	GameOverDialog gameOverDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		points = 0;
		view = new GameView(this);
		gameOverDialog = new GameOverDialog(this);
		handler = new MyHandler(this);
		setLayouts();
		initMusicPlayer();
	}

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
		
		//------------ Ad ---------------
		AdView adView = new AdView(this);
		adView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
		adView.setAdSize(AdSize.BANNER);
		//-------------------------------

		mainLayout.addView(adView);
		mainLayout.addView(view);
		
		setContentView(mainLayout);
		
		adView.loadAd(new AdRequest.Builder().build());
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

	/**
	 * What should happen, when an obstacle is passed?
	 */
	public void obstaclePassed(){
		points++;
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
					game.gameOverDialog.init(game.points);
					game.gameOverDialog.show();
					break;
			}
		}
	}
}
