package edu.american.toddlerselfie;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageSlicer {

	int tileWidth;
	int joinerWidth;
	static final int DEFAULT_TILE_WIDTH = 160;
	static final int DEFAULT_JOINER_WIDTH = 80;
	
	static final String JOINER_SHEET = "joiners.bmp";
	static final int JOINER_SRC_WIDTH = 80;
	static final int JOINER_SRC_HEIGHT = 40;
	
	ArrayList<Bitmap> joiners;

	public ImageSlicer(Context ctx) {
		this(ctx, DEFAULT_TILE_WIDTH, DEFAULT_JOINER_WIDTH);
	}
	
	public ImageSlicer(Context ctx, int tileWidth, int joinerWidth) {
		this.joiners = new ArrayList<Bitmap>();
		this.tileWidth = tileWidth;
		this.joinerWidth = joinerWidth;
		
		ImageLoader il = new ImageLoader(ctx);
		Bitmap allJoiners = il.load(JOINER_SHEET);
		System.out.println("Got joiners file");

		Bitmap j;
		for (int offset = 0; offset + JOINER_SRC_HEIGHT < allJoiners.getHeight(); offset += JOINER_SRC_HEIGHT) {
			j = Bitmap.createScaledBitmap(
				Bitmap.createBitmap(allJoiners, 0, offset, JOINER_SRC_WIDTH, JOINER_SRC_HEIGHT),
				tileWidth,
				joinerWidth,
				true
			);

			joiners.add(j);
		}
	}

	public List<PuzzlePiece> puzzlify(Bitmap img) {
		return puzzlify(img, this.tileWidth, this.joinerWidth);
	}

	public List<PuzzlePiece> puzzlify(Bitmap img, int tileWidth, int joinerWidth) {

		//Cut up pieces
		PuzzlePiece[][] raws = slice(img, tileWidth, joinerWidth);
		
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
								<< 8;
						leftPixels[ii] =
							(pxMask & 0xFF000000)
							| (leftPixels[ii] & 0x00FFFFFF);
						rightPixels[ii] =
							(~pxMask & 0xFF000000)
							| (rightPixels[ii] & 0x00FFFFFF);

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
				bottom = pieces.get((row + 1) * cols + col).getImage();
				int maskIdx = (int) Math.floor(Math.random() * jSize);
				mask = joiners.get(maskIdx);
				flip = (Math.random() < 0.5) ? 1 : -1;
				
				//Load relevant portions of images
				System.out.println("Loading from " + row + ", " + col + " and " + row + ", " + (col+1));
				System.out.println("Bottom location: " + tileWidth + "x" + joinerWidth + ", offset by " + leftOffset + "x0");
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
							<< 8;
					topPixels[ii] =
						(pxMask & 0xFF000000)
						| (topPixels[ii] & 0x00FFFFFF);
					bottomPixels[ii] =
						(~pxMask & 0xFF000000)
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

		//Split up gap squares
		Bitmap tl, tr, bl, br;
		int[] tlPixels = new int[joinerWidth * joinerWidth];
		int[] trPixels = new int[joinerWidth * joinerWidth];
		int[] blPixels = new int[joinerWidth * joinerWidth];
		int[] brPixels = new int[joinerWidth * joinerWidth];
		for (int row = 0; row < rows - 1; row++) {
			if (row == 0)
				topOffset = tileWidth + (int) (joinerWidth / 2);
			else
				topOffset = tileWidth + joinerWidth;
			
			for (int col = 0; col < cols - 1; col++) {
				if (col == 0)
					leftOffset = tileWidth + (int) (joinerWidth / 2);
				else
					leftOffset = tileWidth + joinerWidth;

				tl = pieces.get(row * cols + col).getImage();
				tr = pieces.get(row * cols + col + 1).getImage();
				bl = pieces.get((row + 1) * cols + col).getImage();
				br = pieces.get((row + 1) * cols + col + 1).getImage();
				
				tl.getPixels(
					tlPixels,
					0,
					joinerWidth,
					leftOffset,
					topOffset,
					joinerWidth,
					joinerWidth
				);
				tr.getPixels(
					trPixels,
					0,
					joinerWidth,
					0,
					topOffset,
					joinerWidth,
					joinerWidth
				);
				bl.getPixels(
					blPixels,
					0,
					joinerWidth,
					leftOffset,
					0,
					joinerWidth,
					joinerWidth
				);
				br.getPixels(
					brPixels,
					0,
					joinerWidth,
					0,
					0,
					joinerWidth,
					joinerWidth
				);

				ii = 0;
				for (int pxRow = 0; pxRow < joinerWidth; pxRow++) {
					for (int pxCol = 0; pxCol < joinerWidth; pxCol++) {
						if (pxCol < joinerWidth / 2 && pxRow < joinerWidth / 2) {
							//top left
							trPixels[ii] = 0x00000000;
							blPixels[ii] = 0x00000000;
							brPixels[ii] = 0x00000000;
						}
						else if (pxRow < joinerWidth / 2) {
							//top right
							tlPixels[ii] = 0x00000000;
							blPixels[ii] = 0x00000000;
							brPixels[ii] = 0x00000000;
						}
						else if (pxCol < joinerWidth / 2) {
							//bottom left
							tlPixels[ii] = 0x00000000;
							trPixels[ii] = 0x00000000;
							brPixels[ii] = 0x00000000;
						}
						else {
							//bottom right
							tlPixels[ii] = 0x00000000;
							trPixels[ii] = 0x00000000;
							blPixels[ii] = 0x00000000;
						}
						
						ii++;
					}
				}

				//writeback
				tl.setPixels(
					tlPixels,
					0,
					joinerWidth,
					leftOffset,
					topOffset,
					joinerWidth,
					joinerWidth
				);
				tr.setPixels(
					trPixels,
					0,
					joinerWidth,
					0,
					topOffset,
					joinerWidth,
					joinerWidth
				);
				bl.setPixels(
					blPixels,
					0,
					joinerWidth,
					leftOffset,
					0,
					joinerWidth,
					joinerWidth
				);
				br.setPixels(
					brPixels,
					0,
					joinerWidth,
					0,
					0,
					joinerWidth,
					joinerWidth
				);
			}
		}

		//Overlap areas in TOP edge pieces
		leftPixels = new int[(int)(joinerWidth * joinerWidth / 4)];
		rightPixels = new int[(int)(joinerWidth * joinerWidth / 4)];
		for (ii = 0; ii < leftPixels.length; ii++) {
			leftPixels[ii] = 0x00000000;
			rightPixels[ii] = 0x00000000;
		}
		for (int col = 0; col < cols - 1; col++) {
			left = pieces.get(col).getImage();
			right = pieces.get(col + 1).getImage();

			left.setPixels(
				leftPixels,
				0,
				(int)(joinerWidth / 2),
				left.getWidth() - (int)(joinerWidth / 2),
				0,
				(int)(joinerWidth / 2),
				(int)(joinerWidth / 2)
			);
			right.setPixels(
				rightPixels,
				0,
				(int)(joinerWidth / 2),
				0,
				0,
				(int)(joinerWidth / 2),
				(int)(joinerWidth / 2)
			);			
		}
		
		//Overlap areas in BOTTOM edge pieces
		for (int col = 0; col < cols - 1; col++) {
			left = pieces.get((rows - 1) * cols + col).getImage();
			right = pieces.get((rows - 1) * cols + col + 1).getImage();

			left.setPixels(
				leftPixels,
				0,
				(int)(joinerWidth / 2),
				left.getWidth() - (int)(joinerWidth / 2),
				left.getHeight() - (int)(joinerWidth / 2),
				(int)(joinerWidth / 2),
				(int)(joinerWidth / 2)
			);
			right.setPixels(
				rightPixels,
				0,
				(int)(joinerWidth / 2),
				0,
				right.getHeight() - (int)(joinerWidth / 2),
				(int)(joinerWidth / 2),
				(int)(joinerWidth / 2)
			);			
		}

		//Overlap areas in LEFT edge pieces
		for (int row = 0; row < rows - 1; row++) {
			top = pieces.get(row * cols).getImage();
			bottom = pieces.get((row + 1) * cols).getImage();

			top.setPixels(
				leftPixels,
				0,
				(int)(joinerWidth / 2),
				0,
				top.getHeight() - (int)(joinerWidth / 2),
				(int)(joinerWidth / 2),
				(int)(joinerWidth / 2)
			);
			bottom.setPixels(
				leftPixels,
				0,
				(int)(joinerWidth / 2),
				0,
				0,
				(int)(joinerWidth / 2),
				(int)(joinerWidth / 2)
			);			
		}

		//Overlap areas in RIGHT edge pieces
		for (int row = 0; row < rows - 1; row++) {
			top = pieces.get(row * cols + (cols - 1)).getImage();
			bottom = pieces.get((row + 1) * cols + (cols - 1)).getImage();

			top.setPixels(
				leftPixels,
				0,
				(int)(joinerWidth / 2),
				top.getWidth() - (int)(joinerWidth / 2),
				top.getHeight() - (int)(joinerWidth / 2),
				(int)(joinerWidth / 2),
				(int)(joinerWidth / 2)
			);
			bottom.setPixels(
				leftPixels,
				0,
				(int)(joinerWidth / 2),
				bottom.getWidth() - (int)(joinerWidth / 2),
				0,
				(int)(joinerWidth / 2),
				(int)(joinerWidth / 2)
			);			
		}

		return pieces;
	}
	
	private PuzzlePiece[][] slice(Bitmap img, int tileSize, int joinerSize) {
		int imgWidth = img.getWidth(),
			imgHeight = img.getHeight(),
			x = 0, y = 0, w, h,
			rows = imgHeight / (tileSize + joinerSize),
			cols = imgWidth / (tileSize + joinerSize);
			
		PuzzlePiece[][] slices = new PuzzlePiece[rows][cols];

		for (int row = 0; row < rows; row++) {
			if (row == 0 || row == rows - 1)
				h = tileSize + (int) (1.5 * joinerSize);
			else
				h = tileSize + 2 * joinerSize;

			y = Math.max(
				(row * (tileSize + joinerSize)) - (int) (.5 * joinerSize),
				0
			);
			
			for (int col = 0; col < cols; col++) {
				if (col == 0 || col == cols - 1)
					w = tileSize + (int) (1.5 * joinerSize);
				else
					w = tileSize + 2 * joinerSize;
				
				x = Math.max(
					(col * (tileSize + joinerSize)) - (int) (.5 * joinerSize),
					0
				);
				
				System.out.println(x + ", " + y + ", " + w + ", " + h);
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