package edu.american.toddlerselfie;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageSlicer {

	static final int JOINER_WIDTH = 80;
	static final int JOINER_HEIGHT = 40;
	static final String JOINER_SHEET = "puzzle_joiners.bmp";

	ArrayList<Bitmap> joiners;

	public ImageSlicer(Context ctx) {
		this.joiners = new ArrayList<Bitmap>();

		Bitmap allJoiners = new ImageLoader(ctx).load(JOINER_SHEET);
		for (int offset = 0; offset + JOINER_HEIGHT < allJoiners.getHeight(); offset += JOINER_HEIGHT) {
			this.joiners.add(Bitmap.createBitmap(allJoiners, 0, offset, JOINER_WIDTH, JOINER_HEIGHT));
		}
	}

	public Bitmap[][] puzzlify(Bitmap img) {
		return puzzlify(img, JOINER_WIDTH, JOINER_HEIGHT);
	}

	public Bitmap[][] puzzlify(Bitmap img, int tileWidth, int joinerWidth) {
		Bitmap[][] raws = slice(img, tileWidth + joinerWidth, tileWidth + joinerWidth, (int) (-.5 * joinerWidth), (int) (-.5 * joinerWidth));

		return raws;
	}

	public Bitmap[][] slice(Bitmap img, int width, int height, int offsetX, int offsetY) {
		int imgWidth = img.getWidth(), imgHeight = img.getHeight();

		Bitmap[][] ret = new Bitmap[(imgHeight + offsetY) / height][];
		for (Bitmap[] row : ret) {
			row = new Bitmap[(imgWidth + offsetX) / width];
		}

		return ret;
	}
}