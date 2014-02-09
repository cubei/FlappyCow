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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Game extends Activity implements OnTouchListener{
	public static SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
	
	GameView view;
	int points;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		points = 0;
		
		view = new GameView(this);
		view.setOnTouchListener(this);
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
	public boolean onTouch(View v, MotionEvent event) {
		view.onTouch();
		return true;
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
