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

		//Cut up pieces
		PuzzlePiece[][] raws = slice(
			img,
			tileWidth + (2 * joinerWidth),
			joinerWidth,
			(int) (-.5 * joinerWidth)
		);
		
		int rows = raws.length,
			cols = raws[0].length;

		//Flatten 2-d array
		List<PuzzlePiece> pieces = new ArrayList<PuzzlePiece>();
		
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				pieces.add(raws[row][col]);
			}
		}
		
		//Mask horizontal joiners
		Bitmap left, right, mask;
		int[] leftPixels = new int[tileWidth * joinerWidth];
		int[] rightPixels = new int[tileWidth * joinerWidth];
		int[] maskPixels = new int[tileWidth * joinerWidth];
		int jSize = joiners.size(),
			lFlip, rFlip, topOffset, leftOffset,
			ii, jj = 0;
		
		for (int row = 0; row < rows; row++) {
			if (row == 0)
				topOffset = (int) (joinerWidth / 2);
			else
				topOffset = joinerWidth;

			
			for (int col = 0; col < cols - 1; col++) {
				if (col == 0)
					leftOffset = tileWidth - (int) (joinerWidth / 2);
				else
					leftOffset = tileWidth;

				left = pieces.get(row * cols + col).getImage();
				right = pieces.get(row * cols + col + 1).getImage();
				mask = joiners.get((int) Math.floor(Math.random() * jSize));
				lFlip = (Math.random() < 0.5) ? 0xFFFFFFFF : 0x00000000; 
				rFlip = 0 - lFlip;
				
				//Load relevant portions of images
				System.out.println("Loading from " + row + ", " + col);
				System.out.println("Times we've done this: " + jj++);
				System.out.println("Total: " + left.getWidth() + "x" + left.getHeight());
				System.out.println(joinerWidth + "x" + tileWidth + " offset by " + leftOffset + ", " + topOffset);
				left.getPixels(
					leftPixels,
					0,
					joinerWidth,
					leftOffset,
					topOffset,
					joinerWidth,
					tileWidth
				);
				right.getPixels(
					rightPixels,
					0,
					joinerWidth,
					0,
					topOffset,
					joinerWidth,
					tileWidth
				);
				mask.getPixels(
					maskPixels,
					0,
					tileWidth,
					0,
					0,
					tileWidth,
					joinerWidth
				);
				
				//Do the mask
				//This one is more complicated than the top-bottom ones because
				//  the joiners need to be transposed
				ii = 0;
				for (int pxRow = 0; pxRow < tileWidth; pxRow++) {
					for (int pxCol = 0; pxCol < joinerWidth; pxCol++) {
						leftPixels[ii] =
							((lFlip - maskPixels[pxCol * joinerWidth + pxRow]) & 0xFF000000)
							+ (leftPixels[ii] & 0x00FFFFFF);
						rightPixels[ii] =
								((rFlip - maskPixels[pxCol * joinerWidth + pxRow]) & 0xFF000000)
								+ (rightPixels[ii] & 0x00FFFFFF);
						ii++;
					}
				}

				//Put alpha-d values back into the bitmaps
				left.setPixels(
					leftPixels,
					0,
					joinerWidth,
					leftOffset,
					topOffset,
					joinerWidth,
					tileWidth
				);
				right.setPixels(
					rightPixels,
					0,
					joinerWidth,
					0,
					topOffset,
					joinerWidth,
					tileWidth
				);
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
				
				System.out.println(w + ", " + h + " offset by " + x + ", " + y);
				
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