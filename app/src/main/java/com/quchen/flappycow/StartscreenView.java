/**
 * Splashscreen with buttons.
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */
package com.quchen.flappycow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.gms.games.Games;

public class StartscreenView extends View{
	
	private static Bitmap splash = null;
	private static Bitmap logInOut = null;
	private static Bitmap play = null;
	private static Bitmap achievements = null;
	private static Bitmap leaderboard = null;
	private static Bitmap speaker = null;
	private static Bitmap info = null;
	private static Bitmap socket = null;
	
	// Button regions: left, top, right, bottom
	private final static float[] REGION_LOG_IN_OUT = {175/720.0f, 397/1280f, 547/720.0f, 506/1280.0f};
	private final static float[] REGION_PLAY = {169/720.0f, 515/1280f, 553/720.0f, 699/1280.0f};
	private final static float[] REGION_INFO = {585/720.0f, 1141/1280f, 700/720.0f, 1256/1280.0f};
	private final static float[] REGION_SPEAKER = {25/720.0f, 1140/1280f, 140/720.0f, 1255/1280.0f};
	private final static float[] REGION_SOCKET = {233/720.0f, 1149/1280f, 487/720.0f, 1248/1280.0f};
	private final static float[] REGION_ACHIEVEMENT = {176/720.0f, 709/1280f, 316/720.0f, 849/1280.0f};
	private final static float[] REGION_LEADERBOARD = {413/720.0f, 708/1280f, 553/720.0f, 849/1280.0f};
	
	private Rect dstSplash;
	private Rect srcSplash;
	private Rect dstLogInOut;
	private Rect srcLogInOut;
	private Rect dstPlay;
	private Rect srcPlay;
	private Rect dstAchievements;
	private Rect srcAchievements;
	private Rect dstLeaderboard;
	private Rect srcLeaderboard;
	private Rect dstSpeaker;
	private Rect srcSpeaker;
	private Rect dstInfo;
	private Rect srcInfo;
	private Rect dstSocket;
	private Rect srcSocket;
	
	private boolean online;
	private MainActivity mainActivity;

	public StartscreenView(MainActivity context) {
		super(context);
		this.mainActivity = context;
		if(splash == null) {
			splash = Util.getBitmapAlpha8(mainActivity, R.drawable.splash);
		}
		srcSplash = new Rect(0, 0, splash.getWidth(), splash.getHeight());
		if(logInOut == null) {
			logInOut = Util.getBitmapAlpha8(mainActivity, R.drawable.signinout);
		}
		if(play == null) {
			play = Util.getBitmapAlpha8(mainActivity, R.drawable.play_button);
		}
		srcPlay = new Rect(0, 0, play.getWidth(), play.getHeight());
		if(achievements == null) {
			achievements = Util.getBitmapAlpha8(mainActivity, R.drawable.achievement_button);
		}
		srcAchievements = new Rect(0, 0, achievements.getWidth(), achievements.getHeight());
		if(leaderboard == null) {
			leaderboard = Util.getBitmapAlpha8(mainActivity, R.drawable.highscore_button);
		}
		srcLeaderboard = new Rect(0, 0, leaderboard.getWidth(), leaderboard.getHeight());
		if(speaker == null) {
			speaker = Util.getBitmapAlpha8(mainActivity, R.drawable.speaker);
		}
		if(info == null) {
			info = Util.getBitmapAlpha8(mainActivity, R.drawable.about);
		}
		srcInfo = new Rect(0, 0, info.getWidth(), info.getHeight());
		if(socket == null) {
			socket = Util.getBitmapAlpha8(mainActivity, R.drawable.socket);
		}
		
		setWillNotDraw(false);
		setOnline(false);
		setSpeaker(true);
		setSocket(0);
	}
	
	public void setSpeaker(boolean on) {
		if(on) {
			srcSpeaker = new Rect(0, 0, speaker.getWidth(), speaker.getHeight()/2);
		} else {
			srcSpeaker = new Rect(0, speaker.getHeight()/2, speaker.getWidth(), speaker.getHeight());
		}
	}
	
	public void setOnline(boolean online) {
		this.online = online;
		if(online) {
			srcLogInOut = new Rect(0, logInOut.getHeight()/2, logInOut.getWidth(), logInOut.getHeight());
		} else {
			srcLogInOut = new Rect(0, 0, logInOut.getWidth(), logInOut.getHeight()/2);
		}
	}
	
	public void setSocket(int level) {
		srcSocket = new Rect(0, level*socket.getHeight()/4, socket.getWidth(), (level+1)*socket.getHeight()/4);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(splash, srcSplash, dstSplash, null);
		canvas.drawBitmap(logInOut, srcLogInOut, dstLogInOut, null);
		canvas.drawBitmap(play, srcPlay, dstPlay, null);
		canvas.drawBitmap(speaker, srcSpeaker, dstSpeaker, null);
		canvas.drawBitmap(info, srcInfo, dstInfo, null);
		canvas.drawBitmap(socket, srcSocket, dstSocket, null);
		if(online) {
			canvas.drawBitmap(achievements, srcAchievements, dstAchievements, null);
			canvas.drawBitmap(leaderboard, srcLeaderboard, dstLeaderboard, null);
		}
	}
	
	@Override
	public boolean performClick() {
		return super.performClick();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		dstSplash = new Rect(0, 0, getWidth(), getHeight());
		dstLogInOut = new Rect(	(int)(getWidth()*REGION_LOG_IN_OUT[0]),
								(int)(getHeight()*REGION_LOG_IN_OUT[1]),
								(int)(getWidth()*REGION_LOG_IN_OUT[2]),
								(int)(getHeight()*REGION_LOG_IN_OUT[3]));
		dstPlay = new Rect(	(int)(getWidth()*REGION_PLAY[0]),
							(int)(getHeight()*REGION_PLAY[1]),
							(int)(getWidth()*REGION_PLAY[2]),
							(int)(getHeight()*REGION_PLAY[3]));
		dstAchievements = new Rect(	(int)(getWidth()*REGION_ACHIEVEMENT[0]),
									(int)(getHeight()*REGION_ACHIEVEMENT[1]),
									(int)(getWidth()*REGION_ACHIEVEMENT[2]),
									(int)(getHeight()*REGION_ACHIEVEMENT[3]));
		dstLeaderboard = new Rect(	(int)(getWidth()*REGION_LEADERBOARD[0]),
									(int)(getHeight()*REGION_LEADERBOARD[1]),
									(int)(getWidth()*REGION_LEADERBOARD[2]),
									(int)(getHeight()*REGION_LEADERBOARD[3]));
		dstSpeaker = new Rect(	(int)(getWidth()*REGION_SPEAKER[0]),
								(int)(getHeight()*REGION_SPEAKER[1]),
								(int)(getWidth()*REGION_SPEAKER[2]),
								(int)(getHeight()*REGION_SPEAKER[3]));
		dstInfo = new Rect(	(int)(getWidth()*REGION_INFO[0]),
							(int)(getHeight()*REGION_INFO[1]),
							(int)(getWidth()*REGION_INFO[2]),
							(int)(getHeight()*REGION_INFO[3]));
		dstSocket = new Rect(	(int)(getWidth()*REGION_SOCKET[0]),
								(int)(getHeight()*REGION_SOCKET[1]),
								(int)(getWidth()*REGION_SOCKET[2]),
								(int)(getHeight()*REGION_SOCKET[3]));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		performClick();
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			if(	(event.getX() > REGION_LOG_IN_OUT[0] * getWidth())
					&& (event.getX() < REGION_LOG_IN_OUT[2] * getWidth())
					&& (event.getY() > REGION_LOG_IN_OUT[1] * getHeight())
					&& (event.getY() < REGION_LOG_IN_OUT[3] * getHeight()) ) {
				if(online) {
					mainActivity.logout();
				} else {
					mainActivity.login();
				}
			} else if(	(event.getX() > REGION_PLAY[0] * getWidth())
					&& (event.getX() < REGION_PLAY[2] * getWidth())
					&& (event.getY() > REGION_PLAY[1] * getHeight())
					&& (event.getY() < REGION_PLAY[3] * getHeight()) ) {
				mainActivity.startActivity(new Intent("com.quchen.flappycow.Game"));
			} else if(	(event.getX() > REGION_ACHIEVEMENT[0] * getWidth())
					&& (event.getX() < REGION_ACHIEVEMENT[2] * getWidth())
					&& (event.getY() > REGION_ACHIEVEMENT[1] * getHeight())
					&& (event.getY() < REGION_ACHIEVEMENT[3] * getHeight()) ) {
				mainActivity.startActivityForResult(Games.Achievements.getAchievementsIntent(mainActivity.getApiClient()), 0);;
			} else if(	(event.getX() > REGION_LEADERBOARD[0] * getWidth())
					&& (event.getX() < REGION_LEADERBOARD[2] * getWidth())
					&& (event.getY() > REGION_LEADERBOARD[1] * getHeight())
					&& (event.getY() < REGION_LEADERBOARD[3] * getHeight()) ) {
				mainActivity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mainActivity.getApiClient(),
						getResources().getString(R.string.leaderboard_highscore)), 0);
			} else if(	(event.getX() > REGION_SPEAKER[0] * getWidth())
					&& (event.getX() < REGION_SPEAKER[2] * getWidth())
					&& (event.getY() > REGION_SPEAKER[1] * getHeight())
					&& (event.getY() < REGION_SPEAKER[3] * getHeight()) ) {
				mainActivity.muteToggle();
			} else if(	(event.getX() > REGION_INFO[0] * getWidth())
					&& (event.getX() < REGION_INFO[2] * getWidth())
					&& (event.getY() > REGION_INFO[1] * getHeight())
					&& (event.getY() < REGION_INFO[3] * getHeight()) ) {
				mainActivity.startActivity(new Intent("com.quchen.flappycow.About"));
			}
		}
		return true;
	}

}
