package edu.american.toddlerselfie;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageLoader {

	AssetManager manager;
	
	public ImageLoader(Context ctx) {
		this.manager = ctx.getAssets();
	}
	
	public Bitmap load(String path) {
		try {
			return BitmapFactory.decodeStream(this.manager.open(path));
		}
		catch (IOException e) {
			return null;
		}
	}
	
}
