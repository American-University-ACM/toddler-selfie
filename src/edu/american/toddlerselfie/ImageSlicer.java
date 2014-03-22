package edu.american.toddlerselfie;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageSlicer {

	static final int JOINER_WIDTH = 80;
	static final int JOINER_HEIGHT = 40;
	static final String JOINER_SHEET = "joiners.bmp";
	
	ArrayList<Bitmap> joiners;

	public ImageSlicer(Context ctx) {
		this.joiners = new ArrayList<Bitmap>();

		ImageLoader il = new ImageLoader(ctx);
		Bitmap allJoiners = il.load(JOINER_SHEET);
		System.out.println("Got joiners file");

		for (int offset = 0; offset + JOINER_HEIGHT < allJoiners.getHeight(); offset += JOINER_HEIGHT) {
			this.joiners.add(Bitmap.createBitmap(allJoiners, 0, offset, JOINER_WIDTH, JOINER_HEIGHT));
		}
	}

	public List<PuzzlePiece> puzzlify(Bitmap img) {
		return puzzlify(img, JOINER_WIDTH, JOINER_HEIGHT);
	}

	public List<PuzzlePiece> puzzlify(Bitmap img, int tileWidth, int joinerWidth) {
		PuzzlePiece[][] raws = slice(
				img,
				tileWidth + joinerWidth,
				joinerWidth,
				(int) (-.5 * joinerWidth)
				);
		
		int rows = raws.length,
			cols = raws[0].length;
		
		List<PuzzlePiece> pieces = new ArrayList<PuzzlePiece>();
		
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				pieces.add((row * cols) + col, raws[row][col]);
			}
		}
		
		return pieces;
	}
	
	public PuzzlePiece[][] slice(Bitmap img, int tileSize, int overlap, int offset) {
		int imgWidth = img.getWidth(),
			imgHeight = img.getHeight(),
			x, y, w, h,
			rows = (imgHeight - offset) / (tileSize - overlap),
			cols = (imgWidth - offset) / (tileSize - overlap);
		
		PuzzlePiece[][] slices = new PuzzlePiece[rows][cols];
		
		for (int row = 0; row < rows; row++) {
			y = Math.max((row * (tileSize - overlap)) + offset, 0);
			h = Math.min(tileSize, imgHeight - y);
			
			for (int col = 0; col < cols; col++) {
				x = Math.max((col * (tileSize - overlap)) + offset, 0);
				w = Math.min(tileSize, imgWidth - x);
				
				slices[row][col] = new PuzzlePiece(
					Bitmap.createBitmap(img, x, y, w, h),
					new BoundingBox(
						x,
						y,
						x + w,
						y + h
					)
				);
			}
		}
		
		return slices;
	}
}