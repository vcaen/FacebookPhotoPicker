package fr.vadimcaen.facebookphotopicker.networking;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.util.Log;

public class Util{

	public static void getBitmapFromUrl(String src, NetworkCallback<Bitmap> callback) {
		new DownloadImageTask(callback).execute(src);
	}



	static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		NetworkCallback<Bitmap> mCallback;

		public DownloadImageTask(NetworkCallback<Bitmap> callback) {
			mCallback = callback;
		}

		/** The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute() */
		protected Bitmap doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();

				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (IOException e) {
				// Log exception
				Log.e("BitmapFromURL" ,"The image could has not been retreived at " + urls[0]);
				return null;
			}
		}

		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		protected void onPostExecute(Bitmap result) {
			mCallback.execute(result);
		}
	}
}
