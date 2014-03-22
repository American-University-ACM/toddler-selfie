package edu.american.toddlerselfie;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import edu.american.toddlerselfie.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		View v = findViewById(R.id.start);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), CameraHelper.class);
				startActivityForResult(i, 0);
			}
		});
	}

<<<<<<< HEAD
=======
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
		Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
		ImageView v = (ImageView) findViewById(R.id.picture);
		v.setImageBitmap(b);
	}

>>>>>>> 1103ba2b214c4554eafab2124d48d539327fefe6
}
