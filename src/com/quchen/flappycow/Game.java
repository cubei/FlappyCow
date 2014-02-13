/**
 * The Game
 */

package com.quchen.flappycow;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Game extends Activity implements OnDismissListener{
	public static SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
	public static MediaPlayer musicPlayer = null;
	public static boolean musicShouldPlay = false;
	
	/** time interval (ms) you have to press the backbutton twice in to exit */
	private static final long DOUBLE_BACK_TIME = 1000;
	private long backPressed;
	
	private MyHandler handler;	// To do UI things from different threads
	
	GameView view;
	int points;
	GameOverDialog gameOverDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		points = 0;
		view = new GameView(this);
		gameOverDialog = new GameOverDialog(this);
		setLayouts();
		initHandler();
		initMusicPlayer();
	}
	
	private void initHandler(){
		handler = new MyHandler(this);
	}
	
	public void initMusicPlayer(){
		if(musicPlayer == null){
			// to avoid unnecessary reinitialisation
			musicPlayer = MediaPlayer.create(this, R.raw.nyan_cat_theme);
			musicPlayer.setLooping(true);
			musicPlayer.setVolume(MainActivity.volume, MainActivity.volume);
		}
		musicPlayer.seekTo(0);
	}
	
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
	
	@Override
	protected void onPause() {
		view.pause();
		musicPlayer.pause();
		super.onPause();
	}

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
	
	@Override
	public void onBackPressed() {
		if(System.currentTimeMillis() - backPressed < DOUBLE_BACK_TIME){
			super.onBackPressed();
		}else{
			backPressed = System.currentTimeMillis();
			Toast.makeText(this, "Press backbutton again to exit", Toast.LENGTH_LONG).show();
		}
	}

	public void gameOver(){
//		Intent intent = new Intent("com.quchen.flappycow.GameOverScreen");
//		intent.putExtra("points", points);
//		startActivity(intent);
//		finish();
		handler.sendMessage(Message.obtain(handler,0));
//		view.pause();
	}

	public void obsticalPassed(){
		points++;
	}
	
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

	@Override
	public void onDismiss(DialogInterface dialog) {
		finish();
	}
}
