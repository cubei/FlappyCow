/**
 * The pauseButton
 */

package com.quchen.flappycow;

import android.content.Context;

public class PauseButton extends Sprite{
	public PauseButton(GameView view, Context context) {
		super(view, context);
		this.bitmap = createBitmap(context.getResources().getDrawable(R.drawable.pause_button));
		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();
	}
	
	@Override
	public void move(){
		this.x = this.view.getWidth() - this.width;
		this.y = 0;
	}
}