package edu.american.toddlerselfie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class CameraHelper extends Activity {

	private static final int CAMERA_PIC_REQUEST = 1111;
	private ImageView mImage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_fullscreen);

		mImage = (ImageView) findViewById(R.id.camera_image);
		// 1
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			mImage.setImageBitmap(thumbnail);
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			thumbnail = Bitmap.createScaledBitmap(thumbnail, 480, 480, true);
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
			System.out.println(file.getAbsolutePath());
			try {
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);
				fo.write(bytes.toByteArray());
				fo.close();
			} catch (IOException e) {
				Log.wtf(this.getClass().getSimpleName(), "Could not save image", e);
			}
		}
	}
}
