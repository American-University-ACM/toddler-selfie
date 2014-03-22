package edu.american.toddlerselfie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class PuzzleView extends View implements OnTouchListener, OnDragListener {

	FullscreenActivity game;
	WindowManager wm;
	Display display;
	Point size;
	float screenWidth, screenHeight;
	private Map<ImageView, PuzzlePiece> views = new HashMap<ImageView, PuzzlePiece>();

	public PuzzleView(Context context, List<PuzzlePiece> images) {
		super(context);

		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		screenWidth = this.getWidth();
		screenHeight = this.getHeight();
		for (PuzzlePiece piece : images) {
			ImageView iv = new ImageView(context);
			iv.setImageBitmap(piece.getImage());
			iv.setOnTouchListener(this);
			iv.setOnDragListener(this);
			views.put(iv, piece);
		}
		this.addTouchables(toArrayList(views.keySet()));
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
}
