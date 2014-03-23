package edu.american.toddlerselfie;

import java.util.Collections;
import java.util.List;
import java.lang.Object;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import edu.american.toddlerselfie.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	private static final int CAMERA_REQUEST = 1111;
	private boolean hard=false;
	private List<PuzzlePiece> pieces;
	private PuzzleView puzzleView;
	private Context context;
	private Dialog dialog;
	private ViewGroup layout;
	private View viewPressed;
	private int offsetx, offsety;

	@Override
	public void onResume() {
		super.onResume();
		// Update your UI here.
	}
	public int screenWidth,screenHeight;

	public void getSize() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
	}
	
	public void resize()
	{
		getSize();
		//findViewById(R.id.mainLayout).setScaleX(screenWidth/findViewById(R.id.mainLayout).getWidth());
		//findViewById(R.id.mainLayout).setScaleY(screenHeight/findViewById(R.id.mainLayout).getHeight());
		findViewById(R.id.picture).setScaleX(screenWidth/720);
		findViewById(R.id.picture).setScaleY(screenHeight/480);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.activity_fullscreen);
		findViewById(R.id.picture).setVisibility(View.INVISIBLE);
		findViewById(R.id.settingsButton).setVisibility(View.INVISIBLE);
		findViewById(R.id.piecesLayout).setVisibility(View.INVISIBLE);
		findViewById(R.id.settingsButton).setVisibility(View.INVISIBLE);
		resize();
		findViewById(R.id.startEasy).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, CAMERA_REQUEST);
				hard=false;
			}
		});
		findViewById(R.id.startHard).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, CAMERA_REQUEST);
				hard=true;
			}
		});
		findViewById(R.id.settingsButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// custom dialog
				dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				dialog.setContentView(R.layout.puzzle_layout);
				dialog.setCanceledOnTouchOutside(true);
				dialog.findViewById(R.id.startEasy).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						hard=false;
						Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

						startActivityForResult(intent, CAMERA_REQUEST);
					}
				});
				dialog.findViewById(R.id.startHard).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						hard=true;
						Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

						startActivityForResult(intent, CAMERA_REQUEST);
					}
				});

				dialog.findViewById(R.id.resetButton).setOnClickListener(new OnClickListener() {//TODO: remove puzzle pieces

					@Override
					public void onClick(View v) {
						reset();
						dialog.dismiss();
					}
				});
				dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});


	}

	protected void reset()
	{
		Collections.shuffle(pieces);
		for (int i = 0; i < pieces.size(); i++) {
			ImageView view = (ImageView) ((ViewGroup) findViewById(R.id.piecesLayout)).getChildAt(i);
			view.setImageBitmap(pieces.get(i).getImage());
			view.setId(i);
			view.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					float startX = 0,startY = 0,endX,endY;
					if(event.getAction()==MotionEvent.ACTION_DOWN)
					{
						startX=v.getX();
						startY=v.getY();
						return true;
					}
					else if(event.getAction()==MotionEvent.ACTION_MOVE)
					{
						endX=event.getRawX();
						endY=event.getRawY();
						//System.out.println("difference for x is "+(endX-startX) +", "+ v.getTranslationX() +", "+startX +","+getResources().getResourceEntryName(v.getId()));						
						v.setX(Math.max(0, Math.min(screenWidth-v.getWidth(), endX-v.getHeight()/2)));
						v.setY(Math.max(0, Math.min(screenHeight-v.getHeight(), endY-v.getWidth()/2)));					
						if(pieces.get(v.getId()).correctLocation(v.getX(), v.getY()))
						{
							v.setX((float) pieces.get(v.getId()).getCorrectBoundingBox().xRight-offsetx);
							v.setY((float) pieces.get(v.getId()).getCorrectBoundingBox().yRight-offsety);
							v.setOnTouchListener(null);
						}
						return false;
					}
					return false;
				}
			});
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST) {
			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			thumbnail = Bitmap.createScaledBitmap(thumbnail, 720, 480, true);
			getSize() ;
			ImageView v = (ImageView) findViewById(R.id.picture);

			findViewById(R.id.picture).setVisibility(View.VISIBLE);
			findViewById(R.id.startEasy).setVisibility(View.GONE);
			findViewById(R.id.startHard).setVisibility(View.GONE);
			findViewById(R.id.settingsButton).setVisibility(View.VISIBLE);
			findViewById(R.id.title).setVisibility(View.GONE); 
			ImageSlicer imageSlice;
			System.out.println("this is the result code "+resultCode);
			if(hard)
			{
				imageSlice= new ImageSlicer(this,80,40);
				offsetx=-140;
				offsety=39;
				System.out.println("YES WE REACHED THIS");
			}
			else
			{
				imageSlice= new ImageSlicer(this);
				offsetx=0;
				offsety=180;
				System.out.println("YES WE REACHED THIS 2");
			}
			pieces=imageSlice.puzzlify(thumbnail);
			Collections.shuffle(pieces);
			for (int i = 0; i < pieces.size(); i++) {
				ImageView view = (ImageView) ((ViewGroup) findViewById(R.id.piecesLayout)).getChildAt(i);
				view.setImageBitmap(pieces.get(i).getImage());
				view.setId(i);
				view.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						float startX = 0,startY = 0,endX,endY;
						if(event.getAction()==MotionEvent.ACTION_DOWN)
						{
							startX=v.getX();
							startY=v.getY();
							return true;
						}
						else if(event.getAction()==MotionEvent.ACTION_MOVE)
						{
							endX=event.getRawX();
							endY=event.getRawY();
							//System.out.println("difference for x is "+(endX-startX) +", "+ v.getTranslationX() +", "+startX +","+getResources().getResourceEntryName(v.getId()));						
							v.setX(Math.max(0, Math.min(screenWidth-v.getWidth(), endX-v.getHeight()/2)));
							v.setY(Math.max(0, Math.min(screenHeight-v.getHeight(), endY-v.getWidth()/2)));					
							if(pieces.get(v.getId()).correctLocation(v.getX()+offsetx, v.getY()+offsety))
							{
								v.setX((float) pieces.get(v.getId()).getCorrectBoundingBox().xLeft-offsetx);
								v.setY((float) pieces.get(v.getId()).getCorrectBoundingBox().yLeft-offsety);
								v.setOnTouchListener(null);
							}
							return false;
						}
						return false;
					}

				});
			}
			findViewById(R.id.piecesLayout).setVisibility(View.VISIBLE);
		}
	}
}
