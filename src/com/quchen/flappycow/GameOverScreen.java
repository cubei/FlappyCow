package com.quchen.flappycow;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverScreen extends Activity {
	public static final String score_save_name = "score_save";
	public static final String best_score_key = "score";
	
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
	}
}
