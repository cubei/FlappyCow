/**
 * The dialog shown when the game is over
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

package com.quchen.flappycow;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameOverDialog extends Dialog {
    public static final int REVIVE_PRICE = 5;
    
    /** Name of the SharedPreference that saves the score */
    public static final String score_save_name = "score_save";
    
    /** Key that saves the score */
    public static final String best_score_key = "score";
    
    /** The game that invokes this dialog */
    private Game game;
    
    private TextView tvCurrentScoreVal;
    private TextView tvBestScoreVal;

    public GameOverDialog(Game game) {
        super(game);
        this.game = game;
        this.setContentView(R.layout.gameover);
        this.setCancelable(false);
        
        tvCurrentScoreVal = (TextView) findViewById(R.id.tv_current_score_value);
        tvBestScoreVal = (TextView) findViewById(R.id.tv_best_score_value);
    }
    
    public void init(){
        Button okButton = (Button) findViewById(R.id.b_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCoins();
                if(game.numberOfRevive <= 1){
                    game.accomplishmentBox.saveLocal(game);
                    if(game.getApiClient().isConnected()){
                        game.accomplishmentBox.submitScore(game, game.getApiClient());
                        AccomplishmentBox.savesAreOnline(game);
                    }else{
                        AccomplishmentBox.savesAreOffline(game);
                    }
                }
                
                dismiss();
                game.finish();
            }
        });

        Button reviveButton = (Button) findViewById(R.id.b_revive);
        reviveButton.setText(game.getResources().getString(R.string.revive_button)
                            + " " + REVIVE_PRICE * game.numberOfRevive + " "
                            + game.getResources().getString(R.string.coins));
        reviveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                game.coins -= REVIVE_PRICE * game.numberOfRevive;
                saveCoins();
                game.view.revive();
            }
        });
        if(game.coins < REVIVE_PRICE * game.numberOfRevive){
            reviveButton.setClickable(false);
        }else{
            reviveButton.setClickable(true);
        }
        
        manageScore();
        manageMedals();
    }
    
    private void manageScore(){
        SharedPreferences saves = game.getSharedPreferences(score_save_name, 0);
        int oldPoints = saves.getInt(best_score_key, 0);
        if(game.accomplishmentBox.points > oldPoints){
            // Save new highscore
            SharedPreferences.Editor editor = saves.edit();
            editor.putInt(best_score_key, game.accomplishmentBox.points);
            tvBestScoreVal.setTextColor(Color.RED);
            editor.commit();
        }
        tvCurrentScoreVal.setText("" + game.accomplishmentBox.points);
        tvBestScoreVal.setText("" + oldPoints);
    }
    
    private void manageMedals(){
        SharedPreferences medaille_save = game.getSharedPreferences(MainActivity.medaille_save, 0);
        int medaille = medaille_save.getInt(MainActivity.medaille_key, 0);
      
        SharedPreferences.Editor editor = medaille_save.edit();

        if(game.accomplishmentBox.achievement_gold){
            ((ImageView)findViewById(R.id.medaille)).setImageBitmap(Util.getScaledBitmapAlpha8(game, R.drawable.gold));
            if(medaille < 3){
                editor.putInt(MainActivity.medaille_key, 3);
            }
        }else if(game.accomplishmentBox.achievement_silver){
            ((ImageView)findViewById(R.id.medaille)).setImageBitmap(Util.getScaledBitmapAlpha8(game, R.drawable.silver));
            if(medaille < 2){
                editor.putInt(MainActivity.medaille_key, 2);
            }
        }else if(game.accomplishmentBox.achievement_bronze){
            ((ImageView)findViewById(R.id.medaille)).setImageBitmap(Util.getScaledBitmapAlpha8(game, R.drawable.bronce));
            if(medaille < 1){
                editor.putInt(MainActivity.medaille_key, 1);
            }
        }else{
            ((ImageView)findViewById(R.id.medaille)).setVisibility(View.INVISIBLE);
        }
        editor.commit();
    }
    
    private void saveCoins(){
        SharedPreferences coin_save = game.getSharedPreferences(Game.coin_save, 0);
        coin_save.getInt(Game.coin_key, 0);
        SharedPreferences.Editor editor = coin_save.edit();
        editor.putInt(Game.coin_key, game.coins);
        editor.commit();
    }
    
}
