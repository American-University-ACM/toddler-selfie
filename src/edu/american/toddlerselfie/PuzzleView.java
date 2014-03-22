package edu.american.toddlerselfie;

import android.content.Context;
import android.graphics.Point;
import android.view.*;

public class PuzzleView extends View {

	FullscreenActivity game;
	WindowManager wm;
	Display display;
	Point size;
	float screenWidth,screenHeight;
	
	public PuzzleView (Context context)
	{
		super(context);

		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);		 
		display = wm.getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		screenWidth = this.getWidth();
		screenHeight = this.getHeight();
		
	}
}
