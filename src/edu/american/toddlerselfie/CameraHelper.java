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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 1
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		{
			finishActivity(0);
		}
	}
}
