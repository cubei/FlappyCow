/**
 * The template for every game object
 * 
 * @author Lars Harmsen
 * Copyright (c) <2014> <Lars Harmsen - Quchen>
 */

package com.quchen.flappycow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public abstract class Sprite {

	/** The bitmaps that holds the frames that should be drawn */
	protected Bitmap bitmap;
	
	/** Height and width of one frame of the bitmap */
	protected int height, width;
	
	/** x and y coordinates on the canvas */
	protected int x, y;
	
	/** Horizontal and vertical speed of the sprite */
	protected float speedX, speedY;
	
	/** The source frame of the bitmap that should be drawn */
	protected Rect src;
	
	/** The destination area that the frame should be drawn to */
	protected Rect dst;
	
	/** Coordinates of the frame in the spritesheet */
	protected byte col, row;
	
	/** Number of columns the sprite has */
	protected byte colNr = 1;
	
	/** How long a frame should be displayed */
	protected short frameTime;
	
	/**
	 * Counter for the frames
	 * Cycling through the columns
	 */
	protected short frameTimeCounter;
	
	/** The GameView that holds this Sprite */
	protected GameView view;
	
	/** The context */
	protected Game game;
	
	public Sprite(GameView view, Game game){
		this.view = view;
		this.game = game;
		frameTime = 1;
	}
	
	/**
	 * Draws the frame of the bitmap specified by col and row
	 * at the position given by x and y
	 * @param canvas Canvas that should be drawn on
	 */
	public void draw(Canvas canvas){
		src = new Rect(col*width, row*height, (col+1)*width, (row+1)*height);
		dst = new Rect(x, y, x+width, y+height);
		canvas.drawBitmap(bitmap, src, dst, null);
	}
	
	/**
	 * Modifies the x and y coordinates according to the speedX and speedY value
	 * Also changes the frame by cycling through the columns.
	 */
	public void move(){
		this.frameTimeCounter++;
		if(this.frameTimeCounter >= this.frameTime){
			this.col = (byte) ((this.col+1) % this.colNr);
			this.frameTimeCounter = 0;
		}
		x+= speedX;
		y+= speedY;
	}
	
	/**
	 * Checks whether this sprite is so far to the left, it's not visible anymore.
	 * @return
	 */
	public boolean isOutOfRange(){
		return this.x + width < 0;
	}
	
	/**
	 * Checks whether the sprite is touching this.
	 * Seeing the sprites as rectangles.
	 * @param sprite
	 * @return
	 */
	public boolean isColliding(Sprite sprite){
		if(this.x + getCollisionTolerance() < sprite.x + sprite.width
				&& this.x + this.width > sprite.x + getCollisionTolerance()
				&& this.y + getCollisionTolerance() < sprite.y + sprite.height
				&& this.y + this.height > sprite.y + getCollisionTolerance()){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the sprite is touching this.
	 * With the distance of the 2 centers.
	 * @param sprite
	 * @return
	 */
	public boolean isCollidingRadius(Sprite sprite, float factor){
		int m1x = this.x+(this.width>>1);
		int m1y = this.y+(this.height>>1);
		int m2x = sprite.x+(sprite.width>>1);
		int m2y = sprite.y+(sprite.height>>1);
		int dx = m1x - m2x;
		int dy = m1y - m2y;
		int d = (int) Math.sqrt(dy*dy + dx*dx);
		
		if(d < (this.width + sprite.width) * factor
			|| d < (this.height + sprite.height) * factor){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Checks whether the point specified by the x and y coordinates is touching the sprite.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isTouching(int x, int y){
		return (x  > this.x && x  < this.x + width
			&& y  > this.y && y < this.y + height);
	}
	
	/**
	 * What should be done, when the player collide with this sprite?
	 */
	public void onCollision(){
		
	}
	
	/**
	 * Checks whether the sprite is touching the ground or the sky.
	 * @return
	 */
	public boolean isTouchingEdge(){
		return isTouchingGround() || isTouchingSky();
	}
	
	/**
	 * Checks whether the sprite is touching the ground.
	 * @return
	 */
	public boolean isTouchingGround(){
		return this.y + this.height > this.view.getHeight() - this.view.getHeight() * Frontground.GROUND_HEIGHT;
	}
	
	/**
	 * Checks whether the sprite is touching the sky.
	 * @return
	 */
	public boolean isTouchingSky(){
		return this.y < 0;
	}
	
	/**
	 * Checks whether the play has passed this sprite.
	 * @return
	 */
	public boolean isPassed(){
		return this.x + this.width < view.getPlayer().getX();
	}
	
	/**
	 * What should be done, when the player passes this sprite?
	 */
	public void onPass(){
		
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getSpeedX() {
		return speedX;
	}

	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}
	
	/**
	 * Creates a bitmap that is scales with the getScaleFactor method.
	 * @param drawable
	 * @return
	 */
	public Bitmap createBitmap(Drawable drawable){
		return createBitmap(drawable, game);
	}
	
	/**
	 * Creates a bitmap that is scales with the getScaleFactor method.
	 * Static so everyone, who provides a Context, can use it.
	 * @param drawable
	 * @return
	 */
	public static Bitmap createBitmap(Drawable drawable, Context context){
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bm = bd.getBitmap();
		return Bitmap.createScaledBitmap(bm,
				(int)(bm.getWidth() * getScaleFactor(context)),
				(int)(bm.getHeight() * getScaleFactor(context)),
				false);
	}
	
	/**
	 * Returns a scale factor related to the screen resolution that is used for scaling bitmaps.
	 * @param context
	 * @return
	 */
	public static float getScaleFactor(Context context){
		// 1.2 @ 720x1280 px
		return context.getResources().getDisplayMetrics().heightPixels / 1066f;
	}
	
	/**
	 * Gives a value that will be tolerated when touching a sprite.
	 * Because my images have some whitespace to the edge.
	 * @return
	 */
	private int getCollisionTolerance(){
		// 25 @ 720x1280 px
		return game.getResources().getDisplayMetrics().heightPixels / 50;
	}

}