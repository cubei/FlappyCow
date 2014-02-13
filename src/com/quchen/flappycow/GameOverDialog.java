package com.quchen.flappycow;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameOverDialog extends Dialog {
	public static final String score_save_name = "score_save";
	public static final String best_score_key = "score";
	
	public static final int GOLD_POINTS = 100;
	public static final int SILVER_POINTS = 50;
	public static final int BRONZE_POINTS = 10;
	
	private Game game;

	public GameOverDialog(Game game) {
		super(game);
		this.game = game;
		this.setContentView(R.layout.gameover);
		this.setCancelable(false);
		this.setOnDismissListener(game);
	}
	
	public void init(int points){
//      TextView tvCurrentScore = (TextView) findViewById(R.id.tv_current_score);
      TextView tvCurrentScoreVal = (TextView) findViewById(R.id.tv_current_score_value);
//      TextView tvBestScore = (TextView) findViewById(R.id.tv_best_score);
      TextView tvBestScoreVal = (TextView) findViewById(R.id.tv_best_score_value);
      
      tvCurrentScoreVal.setText("" + points);
      
      SharedPreferences saves = game.getSharedPreferences(score_save_name, 0);
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
				dismiss();
				game.finish();
			}
		});
      
      SharedPreferences medaille_save = game.getSharedPreferences(MainActivity.medaille_save, 0);
      int medaille = medaille_save.getInt(MainActivity.medaille_key, 0);
      
      SharedPreferences.Editor editor = medaille_save.edit();

      if(points >= GOLD_POINTS){
      	((ImageView)findViewById(R.id.medaille)).setImageBitmap(Sprite.createBitmap(game.getResources().getDrawable(R.drawable.gold), game));
      	if(medaille < 3){
	        	editor.putInt(MainActivity.medaille_key, 3);
	        	editor.commit();
      	}
      }else if(points >= SILVER_POINTS){
      	((ImageView)findViewById(R.id.medaille)).setImageBitmap(Sprite.createBitmap(game.getResources().getDrawable(R.drawable.silver), game));
      	if(medaille < 2){
	        	editor.putInt(MainActivity.medaille_key, 2);
	        	editor.commit();
      	}
      }else if(points >= BRONZE_POINTS){
      	((ImageView)findViewById(R.id.medaille)).setImageBitmap(Sprite.createBitmap(game.getResources().getDrawable(R.drawable.bronce), game));
      	if(medaille < 1){
	        	editor.putInt(MainActivity.medaille_key, 1);
	        	editor.commit();
      	}
      }else{
      	((ImageView)findViewById(R.id.medaille)).setVisibility(View.INVISIBLE);
      }
	}
	
}
