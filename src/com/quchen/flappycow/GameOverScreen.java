package com.quchen.flappycow;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameOverScreen extends Activity {
	public static final String score_save_name = "score_save";
	public static final String best_score_key = "score";
	
	public static final int gold = 100;
	public static final int silver = 50;
	public static final int bronce = 10;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.gameover);
        
        TextView tvCurrentScore = (TextView) findViewById(R.id.tv_current_score);
        TextView tvCurrentScoreVal = (TextView) findViewById(R.id.tv_current_score_value);
        TextView tvBestScore = (TextView) findViewById(R.id.tv_best_score);
        TextView tvBestScoreVal = (TextView) findViewById(R.id.tv_best_score_value);
        
        int points = this.getIntent().getExtras().getInt("points");
        tvCurrentScoreVal.setText("" + points);
        
        SharedPreferences saves = this.getSharedPreferences(score_save_name, 0);
        int oldPoints = saves.getInt(best_score_key, 0);
        if(points > oldPoints){
        	SharedPreferences.Editor editor = saves.edit();
        	editor.putInt(best_score_key, points);
        	tvBestScoreVal.setTextColor(Color.RED);
        	editor.commit();
        }
        
        tvBestScoreVal.setText("" + oldPoints);
        
        Button okButton = (Button) findViewById(R.id.b_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        SharedPreferences medaille_save = this.getSharedPreferences(MainActivity.medaille_save, 0);
        int medaille = medaille_save.getInt(MainActivity.medaille_key, 0);
        
        SharedPreferences.Editor editor = medaille_save.edit();

        if(points >= gold){
        	((ImageView)findViewById(R.id.medaille)).setImageDrawable(getResources().getDrawable(R.drawable.gold));
        	if(medaille < 3){
	        	editor.putInt(MainActivity.medaille_key, 3);
	        	editor.commit();
        	}
        }else if(points >= silver){
        	((ImageView)findViewById(R.id.medaille)).setImageDrawable(getResources().getDrawable(R.drawable.silver));
        	if(medaille < 2){
	        	editor.putInt(MainActivity.medaille_key, 2);
	        	editor.commit();
        	}
        }else if(points >= bronce){
        	((ImageView)findViewById(R.id.medaille)).setImageDrawable(getResources().getDrawable(R.drawable.bronce));
        	if(medaille < 1){
	        	editor.putInt(MainActivity.medaille_key, 1);
	        	editor.commit();
        	}
        }else{
        	((ImageView)findViewById(R.id.medaille)).setVisibility(View.INVISIBLE);
        }
        
	}
}
