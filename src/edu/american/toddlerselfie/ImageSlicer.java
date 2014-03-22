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
		
		//Mask left-right joiners
		Bitmap left, right, mask;
		int[] leftPixels = new int[tileWidth * joinerWidth];
		int[] rightPixels = new int[tileWidth * joinerWidth];
		int[] maskPixels = new int[tileWidth * joinerWidth];
		int jSize = joiners.size(),
			flip, topOffset, leftOffset,
			ii, pxMask;
		
		jjjjj: for (int row = 0; row < rows; row++) {
			if (row == 0)
				topOffset = (int) (joinerWidth / 2);
			else
				topOffset = joinerWidth;

			
			for (int col = 0; col < cols - 1; col++) {
				if (col == 0)
					leftOffset = tileWidth + (int) (joinerWidth / 2);
				else
					leftOffset = tileWidth + joinerWidth;

//				if (col != 0) break jjjjj;
				
				left = pieces.get(row * cols + col).getImage();
				right = pieces.get(row * cols + col + 1).getImage();
				int maskIdx = (int) Math.floor(Math.random() * jSize);
				mask = joiners.get(maskIdx);
				flip = (Math.random() < 0.5) ? 1 : -1;
				
				//Load relevant portions of images
				System.out.println("Loading from " + row + ", " + col + " and " + row + ", " + (col+1));
				System.out.println("Using joiner " + maskIdx);
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
				pxMask = 0;
				for (int pxRow = 0; pxRow < tileWidth; pxRow++) {
					for (int pxCol = 0; pxCol < joinerWidth; pxCol++) {
						pxMask = maskPixels[pxCol * tileWidth + pxRow]
								<< 8
								& 0xFF000000;
//						System.out.println("Mask value at " + pxRow + ", " + pxCol + ": " + Integer.toHexString(maskPixels[pxCol * tileWidth + pxRow]));
//						System.out.println("Computed value: " + Integer.toHexString(pxMask));
						leftPixels[ii] =
							pxMask
							| (leftPixels[ii] & 0x00FFFFFF);
						rightPixels[ii] =
								(0 - pxMask)
								| (rightPixels[ii] & 0x00FFFFFF);

//						System.out.println("Final: " + leftPixels[ii]);
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
		} //Done left-right
		
		//Mask top-bottom joiners
		Bitmap top, bottom;
		int[] topPixels = leftPixels;
		int[] bottomPixels = rightPixels;
		
		for (int row = 0; row < rows - 1; row++) {
			if (row == 0)
				topOffset = tileWidth + (int) (joinerWidth / 2);
			else
				topOffset = tileWidth + joinerWidth;
			
			for (int col = 0; col < cols; col++) {
				if (col == 0)
					leftOffset = (int) (joinerWidth / 2);
				else
					leftOffset = joinerWidth;
				
				top = pieces.get(row * cols + col).getImage();
				bottom = pieces.get(row * cols + col + 1).getImage();
				int maskIdx = (int) Math.floor(Math.random() * jSize);
				mask = joiners.get(maskIdx);
				flip = (Math.random() < 0.5) ? 1 : -1;
				
				//Load relevant portions of images
				System.out.println("Loading from " + row + ", " + col + " and " + row + ", " + (col+1));
				System.out.println("Using top-bottom joiner " + maskIdx);
				top.getPixels(
					topPixels,  //array
					0,          //offset
					tileWidth,  //stride
					leftOffset, //x
					topOffset,  //y
					tileWidth,  //w
					joinerWidth //h
				);
				bottom.getPixels(
					bottomPixels, //array
					0,            //offset
					tileWidth,    //stride
					leftOffset,   //x
					0,            //y
					tileWidth,    //w
					joinerWidth   //h
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
				for (ii = 0; ii < topPixels.length; ii++) {
					pxMask = maskPixels[ii]
							<< 8
							& 0xFF000000;
	//						System.out.println("Mask value at " + pxRow + ", " + pxCol + ": " + Integer.toHexString(maskPixels[pxCol * tileWidth + pxRow]));
	//						System.out.println("Computed value: " + Integer.toHexString(pxMask));
					topPixels[ii] =
						pxMask
						| (topPixels[ii] & 0x00FFFFFF);
					bottomPixels[ii] =
							(0 - pxMask)
							| (bottomPixels[ii] & 0x00FFFFFF);
				}

				//Put alpha-d values back into the bitmaps
				top.setPixels(
					topPixels,  //array
					0,          //offset
					tileWidth,  //stride
					leftOffset, //x
					topOffset,  //y
					tileWidth,  //w
					joinerWidth //h
				);
				bottom.setPixels(
					bottomPixels, //array
					0,            //offset
					tileWidth,    //stride
					leftOffset,   //x
					0,            //y
					tileWidth,    //w
					joinerWidth   //h
				);
			}
		}
		return pieces;
	}
	
	private PuzzlePiece[][] slice(Bitmap img, int tileSize, int overlap, int offset) {
		int imgWidth = img.getWidth(),
			imgHeight = img.getHeight(),
			x, y, w, h,
			rows = (imgHeight - offset) / (tileSize - overlap),
			cols = (imgWidth - offset) / (tileSize - overlap);
		
		PuzzlePiece[][] slices = new PuzzlePiece[rows][cols];
		
		for (int row = 0; row < rows; row++) {
			y = (row * (tileSize - overlap)) + offset;
			h = Math.min(tileSize, imgHeight - y);
			if (y < 0) {
				h += y;
				y = 0;
			}
			
			for (int col = 0; col < cols; col++) {
				x = (col * (tileSize - overlap)) + offset;
				w = Math.min(tileSize, imgWidth - x);
				if (x < 0) {
					w += x;
					x = 0;
				}
				
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