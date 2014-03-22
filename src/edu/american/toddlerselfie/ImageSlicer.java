package edu.american.toddlerselfie;

import java.util.ArrayList;

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

	public Bitmap[][] puzzlify(Bitmap img) {
		return puzzlify(img, JOINER_WIDTH, JOINER_HEIGHT);
	}

	public Bitmap[][] puzzlify(Bitmap img, int tileWidth, int joinerWidth) {
		Bitmap[][] raws = slice(
				img,
				tileWidth + joinerWidth,
				joinerWidth,
				(int) (-.5 * joinerWidth)
				);
		
		return raws;
	}
	
	public Bitmap[][] slice(Bitmap img, int tileSize, int overlap, int offset) {
		int imgWidth = img.getWidth(),
			imgHeight = img.getHeight(),
			x, y, w, h,
			rows = (imgHeight - offset) / (tileSize - overlap),
			cols = (imgWidth - offset) / (tileSize - overlap);
		
		Bitmap[][] slices = new Bitmap[rows][cols];
		
		for (int row = 0; row < rows; row++) {
			y = Math.max((row * (tileSize - overlap)) + offset, 0);
			h = Math.min(tileSize, imgHeight - y);
			
			for (int col = 0; col < cols; col++) {
				x = Math.max((col * (tileSize - overlap)) + offset, 0);
				w = Math.min(tileSize, imgWidth - x);
				
				slices[row][col] = Bitmap.createBitmap(img,	x, y, w, h);
			}
		}
		
		return slices;
	}
}