/**
 * Saves achievements and score in shared preferences.
 * You should use a SQLite DB instead, but I'm too lazy to chance it now.
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

package com.quchen.flappycow;

import com.google.android.gms.games.Games;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

public class AccomplishmentBox{
    /** Points needed for a gold medal */
    public static final int GOLD_POINTS = 100;
    
    /** Points needed for a silver medal */
    public static final int SILVER_POINTS = 50;
    
    /** Points needed for a bronze medal */
    public static final int BRONZE_POINTS = 10;
    
    public static final String SAVE_NAME = "ACCOMBLISHMENTS";
    
    public static final String ONLINE_STATUS_KEY = "online_status";
    
    public static final String KEY_POINTS = "points";
    public static final String ACHIEVEMENT_KEY_50_COINS = "achievement_survive_5_minutes";
    public static final String ACHIEVEMENT_KEY_TOASTIFICATION = "achievement_toastification";
    public static final String ACHIEVEMENT_KEY_BRONZE = "achievement_bronze";
    public static final String ACHIEVEMENT_KEY_SILVER = "achievement_silver";
    public static final String ACHIEVEMENT_KEY_GOLD = "achievement_gold";
    
    int points;
    boolean achievement_50_coins;
    boolean achievement_toastification;
    boolean achievement_bronze;
    boolean achievement_silver;
    boolean achievement_gold;
    
    /**
     * Stores the score and achievements locally.
     * 
     * The accomblishments will be saved local via SharedPreferences.
     * This makes it very easy to cheat.
     * 
     * @param activity activity that is needed for shared preferences
     */
    public void saveLocal(Activity activity){
        SharedPreferences saves = activity.getSharedPreferences(SAVE_NAME, 0);
        SharedPreferences.Editor editor = saves.edit();
        
        if(points > saves.getInt(KEY_POINTS, 0)){
            editor.putInt(KEY_POINTS, points);
        }
        if(achievement_50_coins){
            editor.putBoolean(ACHIEVEMENT_KEY_50_COINS, true);
        }
        if(achievement_toastification){
            editor.putBoolean(ACHIEVEMENT_KEY_TOASTIFICATION, true);
        }
        if(achievement_bronze){
            editor.putBoolean(ACHIEVEMENT_KEY_BRONZE, true);
        }
        if(achievement_silver){
            editor.putBoolean(ACHIEVEMENT_KEY_SILVER, true);
        }
        if(achievement_gold){
            editor.putBoolean(ACHIEVEMENT_KEY_GOLD, true);
        }
        
        editor.commit();
    }
    
    /**
     * Uploads accomplishments to Google Play Services
     * @param activity
     * @param apiClient
     */
    public void submitScore(Activity activity, GoogleApiClient apiClient){
        Games.Leaderboards.submitScore(apiClient, activity.getResources().getString(R.string.leaderboard_highscore), this.points);
        
        if(this.achievement_50_coins){
            Games.Achievements.unlock(apiClient, activity.getResources().getString(R.string.achievement_50_coins));
        }
        if(this.achievement_toastification){
            Games.Achievements.unlock(apiClient, activity.getResources().getString(R.string.achievement_toastification));
        }
        if(this.achievement_bronze){
            Games.Achievements.unlock(apiClient, activity.getResources().getString(R.string.achievement_bronze));
        }
        if(this.achievement_silver){
            Games.Achievements.unlock(apiClient, activity.getResources().getString(R.string.achievement_silver));
        }
        if(this.achievement_gold){
            Games.Achievements.unlock(apiClient, activity.getResources().getString(R.string.achievement_gold));
        }
        
        AccomplishmentBox.savesAreOnline(activity);
        
        Toast.makeText(activity.getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * reads the local stored data
     * @param activity activity that is needed for shared preferences
     * @return local stored score and achievements
     */
    public static AccomplishmentBox getLocal(Activity activity){
        AccomplishmentBox box = new AccomplishmentBox();
        SharedPreferences saves = activity.getSharedPreferences(SAVE_NAME, 0);
        
        box.points = saves.getInt(KEY_POINTS, 0);
        box.achievement_50_coins = saves.getBoolean(ACHIEVEMENT_KEY_50_COINS, false);
        box.achievement_toastification = saves.getBoolean(ACHIEVEMENT_KEY_TOASTIFICATION, false);
        box.achievement_bronze = saves.getBoolean(ACHIEVEMENT_KEY_BRONZE, false);
        box.achievement_silver = saves.getBoolean(ACHIEVEMENT_KEY_SILVER, false);
        box.achievement_gold = saves.getBoolean(ACHIEVEMENT_KEY_GOLD, false);
        
        return box;
    }
    
    /**
     * marks the data as online
     * @param activity activity that is needed for shared preferences
     */
    public static void savesAreOnline(Activity activity){
        SharedPreferences saves = activity.getSharedPreferences(SAVE_NAME, 0);
        SharedPreferences.Editor editor = saves.edit();
        editor.putBoolean(ONLINE_STATUS_KEY, true);
        editor.commit();
    }
    
    /**
     * marks the data as offline
     * @param activity activity that is needed for shared preferences
     */
    public static void savesAreOffline(Activity activity){
        SharedPreferences saves = activity.getSharedPreferences(SAVE_NAME, 0);
        SharedPreferences.Editor editor = saves.edit();
        editor.putBoolean(ONLINE_STATUS_KEY, false);
        editor.commit();
    }
    
    /**
     * checks if the last data is already uploaded
     * @param activity activity that is needed for shared preferences
     * @return wheater the last data is already uploaded
     */
    public static boolean isOnline(Activity activity){
        return activity.getSharedPreferences(SAVE_NAME, 0).getBoolean(ONLINE_STATUS_KEY, true);
    }
}