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
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Game extends Activity{
	public static SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
	
	/** time interval (ms) you have to press the backbutton twice in to exit */
	private static final long DOUBLE_BACK_TIME = 1000;
	private long backPressed;
	
	GameView view;
	int points;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		points = 0;
		
		view = new GameView(this);
		setLayouts();
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
		super.onPause();
	}

	@Override
	protected void onResume() {
		view.resume();
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
		Intent intent = new Intent("com.quchen.flappycow.GameOverScreen");
		intent.putExtra("points", points);
		startActivity(intent);
		finish();
	}
	
	public void obsticalPassed(){
		points++;
	}
	
}
