package edu.american.toddlerselfie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
	private static final int CAMERA_PIC_REQUEST = 1111;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		View v = findViewById(R.id.start);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, CAMERA_PIC_REQUEST);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			thumbnail = Bitmap.createScaledBitmap(thumbnail, 480, 480, true);
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
			try {
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);
				fo.write(bytes.toByteArray());
				fo.close();
				Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
				ImageView v = (ImageView) findViewById(R.id.picture);
				v.setImageBitmap(b);
				findViewById(R.id.start).setVisibility(View.INVISIBLE);
			} catch (IOException e) {
				Log.wtf(this.getClass().getSimpleName(), "Could not save image", e);
			}
		}

	}

}
