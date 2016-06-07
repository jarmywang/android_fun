package com.example.user.myapplication.com.itlanbao;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
 
 
public class ImageUtil {
	 
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = null;
		try {
			bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			final Canvas canvas = new Canvas(bitmap);
			// canvas.setBitmap(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
		} catch (OutOfMemoryError e) { 
				e.printStackTrace(); 
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
			System.gc();
		}

		return bitmap;
	}
}
