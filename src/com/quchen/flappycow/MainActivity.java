/**
 * Main Activity / Splashscreen with buttons.
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

package com.quchen.flappycow;

import com.google.android.gms.common.SignInButton;
import com.google.example.games.basegameutils.BaseGameActivity;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends BaseGameActivity {
	
	/** Name of the SharedPreference that saves the medals */
	public static final String medaille_save = "medaille_save";
	
	/** Key that saves the medal */
	public static final String medaille_key = "medaille_key";
	
	public static final float DEFAULT_VOLUME = 0.3f;
	
	/** Volume for sound and music */
	public static float volume = DEFAULT_VOLUME;
	
	private ImageButton muteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        showOfflineButtons();
        
        ((ImageButton)findViewById(R.id.play_button)).setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.play_button), this));
        ((ImageButton)findViewById(R.id.play_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent("com.quchen.flappycow.Game"));
			}
		});
        
        ((ImageButton)findViewById(R.id.highscore_button)).setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.highscore_button), this));
        ((ImageButton)findViewById(R.id.highscore_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(MainActivity.this.mHelper.getGamesClient().getLeaderboardIntent(
						getResources().getString(R.string.leaderboard_highscore)), 0);
			}
		});
        
        ((ImageButton)findViewById(R.id.achievement_button)).setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.achievement_button), this));
        ((ImageButton)findViewById(R.id.achievement_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(MainActivity.this.mHelper.getGamesClient().getAchievementsIntent(),0);
			}
		});
        
        ((SignInButton)findViewById(R.id.sign_in_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				beginUserInitiatedSignIn();
			}
		});
        
        ((Button)findViewById(R.id.sign_out_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				signOut();
				showOfflineButtons();
			}
		}); 
        
        muteButton = ((ImageButton)findViewById(R.id.mute_button));
        muteButton.setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.speaker), this));
        muteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(volume != 0){
					volume = 0;
					muteButton.setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.speaker_mute), MainActivity.this));
				}else{
					volume = DEFAULT_VOLUME;
					muteButton.setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.speaker), MainActivity.this));
				}
			}
		});
        
        ((Button)findViewById(R.id.about_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent("com.quchen.flappycow.About"));
			}
		});
        
        setSocket();
    }
    
	/**
	 * Fills the socket with the medals that have already been collected.
	 */
	private void setSocket(){
		SharedPreferences saves = this.getSharedPreferences(medaille_save, 0);
        switch(saves.getInt(medaille_key, 0)){
        	case 1:
        		((ImageView)findViewById(R.id.medaille_socket)).setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.socket_bronce), this));
        		break;
        	case 2:
        		((ImageView)findViewById(R.id.medaille_socket)).setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.socket_silver), this));
        		break;
        	case 3:
        		((ImageView)findViewById(R.id.medaille_socket)).setImageBitmap(Sprite.createBitmap(getResources().getDrawable(R.drawable.socket_gold), this));
        		break;
        }
	}

	/**
	 * Updates the socket for the medals.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		setSocket();
	}

	@Override
	public void onSignInFailed() {
		Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSignInSucceeded() {
		Toast.makeText(this, "You're logged in", Toast.LENGTH_SHORT).show();
		showOnlineButtons();
		
		if(AccomplishmentBox.isOnline(this)){
			AccomplishmentBox.getLocal(this).submitScore(this, this.mHelper.getGamesClient());
		}
	}
	
	private void showOnlineButtons(){
		findViewById(R.id.achievement_button).setVisibility(View.VISIBLE);
		findViewById(R.id.highscore_button).setVisibility(View.VISIBLE);
		findViewById(R.id.sign_in_button).setVisibility(View.GONE);
		findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
	}
	
	private void showOfflineButtons(){
		findViewById(R.id.achievement_button).setVisibility(View.GONE);
		findViewById(R.id.highscore_button).setVisibility(View.GONE);
		findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
		findViewById(R.id.sign_out_button).setVisibility(View.GONE);
	}
    
}
