package edu.american.toddlerselfie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PuzzleView extends ViewGroup implements OnTouchListener, OnDragListener {

	FullscreenActivity game;
	WindowManager wm;
	Display display;
	Point size;
	float screenWidth, screenHeight;
	private Map<ImageView, PuzzlePiece> views = new HashMap<ImageView, PuzzlePiece>();

	public PuzzleView(Context context, List<PuzzlePiece> images) {
		super(context);

		//wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		//display = wm.getDefaultDisplay();
		//size = new Point();
		//display.getSize(size);
		//screenWidth = this.getWidth();
		//screenHeight = this.getHeight();
		for (PuzzlePiece piece : images) {
			ImageView iv = new ImageView(context);
			iv.setImageBitmap(piece.getImage());
			iv.setOnTouchListener(this)
			iv.setOnDragListener(this);
			iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	
			iv.setX(50);
			iv.setY(50);
			this.addView(iv);
			views.put(iv, piece);	
		}
		this.addTouchables(toArrayList(views.keySet()));
	}
	
	public Map<ImageView, PuzzlePiece> getView()
	{
		return this.views;
	}

	private ArrayList<View> toArrayList(Set<ImageView> keySet) {
		ArrayList<View> vs = new ArrayList<View>();
		for (ImageView v : keySet) {
			vs.add(v);
		}
		return vs;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		if (event.getAction() == DragEvent.ACTION_DROP) {
			ImageView view = (ImageView) event.getLocalState();
			PuzzlePiece p = views.get(view);
			if (p.correctLocation(event.getX(), event.getY())) {
				// snap into place
			} else {
				// keep there

			}
		}
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		PuzzlePiece p = views.get(view);
		if (event.getAction() == MotionEvent.ACTION_DOWN && !p.correctLocation(event.getX(), event.getY())) {
			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
			v.startDrag(null, shadowBuilder, v, 0);
			return true;
		}
		return false;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
	}
}
