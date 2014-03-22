package edu.american.toddlerselfie;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import edu.american.toddlerselfie.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1111;
	private Context context;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.activity_fullscreen);
		findViewById(R.id.picture).setVisibility(View.INVISIBLE);
		findViewById(R.id.start).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, CAMERA_PIC_REQUEST);
			}
		});
		//findViewById(R.id.settingsButton).setVisibility(View.INVISIBLE);

		findViewById(R.id.settingsButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// custom dialog
				dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				dialog.setContentView(R.layout.puzzle_layout);
				dialog.setCanceledOnTouchOutside(true);
				dialog.findViewById(R.id.newButton).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(intent, CAMERA_PIC_REQUEST);
					}
				});

				dialog.findViewById(R.id.resetButton).setOnClickListener(new OnClickListener() {//TODO: remove puzzle pieces

					@Override
					public void onClick(View v) {

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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			thumbnail = Bitmap.createScaledBitmap(thumbnail, 720, 480, true);
			
			ImageView v = (ImageView) findViewById(R.id.picture);
			v.setImageBitmap(thumbnail);

			findViewById(R.id.picture).setVisibility(View.VISIBLE);
			findViewById(R.id.start).setVisibility(View.GONE);
			findViewById(R.id.settingsButton).setVisibility(View.VISIBLE);
		}
	}

}
