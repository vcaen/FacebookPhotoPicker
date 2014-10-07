package fr.vadimcaen.facebookphotopicker.networking;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.model.GraphObject;

import fr.vadimcaen.facebookphotopicker.R;
import fr.vadimcaen.facebookphotopicker.R.drawable;
import fr.vadimcaen.facebookphotopicker.GraphInterface.GraphPicture;

public class FacebookPicture implements Serializable {

	private static final long serialVersionUID = 6211136307410915637L;

	private static final String TAG            = "FacebookPicture";
	private static final String STORAGE_FOLDER = "pictures";
	private static final int    QUALITY        = 50;
	
	private static Bitmap noPictureImage;
	

	private String mId;
	private String mSource;
	private String mPicture;
	private Context mContext;
	private boolean high_definition = false;
	private ArrayList<ImageView> containers = new ArrayList<ImageView>();
	
	private GraphPicture graphPicture;
	
	public FacebookPicture(GraphPicture graphPicture, Context context) {
		super();
		this.graphPicture = graphPicture;
		mSource = graphPicture.getSource();
		mPicture = graphPicture.getPicture();
		mId = graphPicture.getId();
		mContext = context;
	}
	
//	public FacebookPicture(String mId, String mUrl, Context mContext) {
//		super();
//		this.mId = mId;
//		this.mSource = mUrl;
//		this.mContext = mContext;
//		//TODO TEST 
//		getHighDefPicture();
//	}

//	public FacebookPicture(String mId, String mUrl, Context mContext,
//			Bitmap bitmap) {
//		this(mId, mUrl, mContext);
//		saveBitmapOnStorage(bitmap);
//	}
	
	public void getBitmap(NetworkCallback<Bitmap> callback) {
		if(isCached()) {
			Bitmap bitmap = getBitmapFromStorage();
			callback.execute((bitmap != null) ? bitmap : getNoPictureImage());
		} else {
			getBitmapFromNetwork(callback);
		}
	}

	public void getBitmapFromNetwork(final NetworkCallback<Bitmap> callback) {
		getBitmapFromNetwork(callback, false);
	}
	
	public void getBitmapFromNetwork(final NetworkCallback<Bitmap> callback, boolean highDefinition) {
		//*
		String url = (highDefinition) ? mSource : mPicture;
		Util.getBitmapFromUrl(url, new NetworkCallback<Bitmap>() {

			@Override
			public void execute(Bitmap object) {
				if(object != null) {
					saveBitmapOnStorage(object);
					callback.execute(object);
				} else {
					callback.execute(getNoPictureImage());
				}
			}
		});
		//*/
		
		/* TODO TEST
		callback.execute(getNoPictureImage());
		//*/
	}

	public Bitmap getBitmapFromStorage() {
		
		try {
			if (!isCached())
				throw new FileNotFoundException("File is not cached.");
			File mypath = new File(getDirectory(), getFileNameFromId());
			FileInputStream stream = new FileInputStream(mypath);
			return BitmapFactory.decodeStream(stream);
		} catch (FileNotFoundException|NullPointerException e) {
			Log.e(TAG,
					"The image "
							+ mId
							+ " couldn't be found. Please get it from Network using getBitmapFromNetwork.");
			Log.e(TAG, e.getMessage());
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.avatar);
		}
	}

	public void saveBitmapOnStorage(Bitmap bitmap) {
		File mypath = new File(getDirectory(), getFileNameFromId());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to
			// the OutputStream
			bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getHighDefPicture() {
				
		Util.getBitmapFromUrl(mSource, new NetworkCallback<Bitmap>() {

			@Override
			public void execute(Bitmap object) {
				saveBitmapOnStorage(object);
				high_definition = true;

				// We update the given containers with the new image.
				if(containers.size() > 0) {
					for (ImageView v : containers) {
						if(v != null) {
							v.setImageBitmap(getBitmapFromStorage());
						}
					}
				}
			}
		});
	}

	/**
	 * Associate an ImageView with the Facebook Picture that will be updated once
	 * the high definition version of the picture is downloaded.
	 * If you don't use it, you might have
	 * @param container
	 */
	public void updateImageViewOnHighDefinitionRetrieved(ImageView container) {
		if(!containers.contains(container)) { 
			containers.add(container);
		}
		
		if(high_definition) {
			container.setImageBitmap(getBitmapFromStorage());
		} else {
			getHighDefPicture();
		}
	}
	
	/**
	 * @return the mId
	 */
	public String getId() {
		return mId;
	}

	/**
	 * @return the mUrl
	 */
	public String getmUrl() {
		return mSource;
	}

	private String getFileNameFromId() {
		return mId;
	}
	
	private File getDirectory() throws NullPointerException {
		try {
			ContextWrapper cw = new ContextWrapper(mContext);
			// path to /data/data/yourapp/app_data/imageDir
			return  cw.getDir(STORAGE_FOLDER, Context.MODE_PRIVATE);
		} catch (NullPointerException e) {
			Log.w(TAG, "No Context to get the directory");
			e.printStackTrace();
			return null;
		}
	}

	private boolean isCached() {
		File file = new File(getDirectory(), getFileNameFromId());
		
//		for(String s : getDirectory().list())
//		Log.d("PictureDirectory", s);
		return file.exists();
	}

	/**
	 * @param mId
	 *            the mId to set
	 */
	public void setmId(String mId) {
		this.mId = mId;
	}

	/**
	 * @param mUrl
	 *            the mUrl to set
	 */
	public void setmUrl(String mUrl) {
		this.mSource = mUrl;
	}
	
	private Bitmap getNoPictureImage() {
		if (noPictureImage == null) {
			noPictureImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.avatar);
		}
		return noPictureImage;
	}
	
	@Override
	public boolean equals(Object o) {
		if(! (o instanceof FacebookPicture)) {
			return false;
		}
		if(!((FacebookPicture) o).getId().equals(this.getId())) {
			return false;
		}
		return true;
		
	}

}
