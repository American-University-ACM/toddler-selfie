package edu.american.toddlerselfie;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PuzzlePiece {

	private Bitmap m;
	private BoundingBox bb;

	public PuzzlePiece(Bitmap m, BoundingBox box) {
		this.m = m;
		this.bb = box;
	}

	public Bitmap getImage() {
		return m;
	}

	public BoundingBox getCorrectBoundingBox() {
		return bb;
	}
	
	public boolean correctLocation(double x, double y) {
		return bb.contains(x, y);
	}
}
