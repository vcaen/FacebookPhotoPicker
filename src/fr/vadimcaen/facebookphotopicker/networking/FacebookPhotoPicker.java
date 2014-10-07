package fr.vadimcaen.facebookphotopicker.networking;
import android.app.Activity;
import android.os.Bundle;

import com.facebook.Session;

import fr.vadimcaen.facebookphotopicker.networking.PickerDialog.OnPictureSelected;


public class FacebookPhotoPicker {
	
	private static final int DEFAULT_PHOTO_LOAD_COUNT = 10;
	
	Session mSession;
	Activity mActivity;
	ReturnCallback mCallback;
	
	
	
	public FacebookPhotoPicker(Activity activity, Session session) {
		this(activity);
		mSession = session;
		
	}
	
	public FacebookPhotoPicker(Activity activity) {
		mActivity = activity;
		mSession = Session.getActiveSession();
		mCallback = new DefaultReturnCallback();
	}
	
	public 
		PickerDialog settingsDialog = new PickerDialog();
	
	
	public interface ReturnCallback {
		public void execute(String pictureID);
	}
	
	private class DefaultReturnCallback implements ReturnCallback {
		@Override
		public void execute(String pictureID) {
			
		}
	}
	
	public static class Builder {
		
		Session mSession;
		Activity mActivity;
		ReturnCallback mCallback;
		Bundle arguments = new Bundle();
		
		public Builder(Activity activity) {
			mActivity = activity;
		}
		
		public Builder setSession(Session session) {
			mSession = session;
			return this;
		}
		
		public Builder setOnReturnCallback(ReturnCallback callback) {
			mCallback = callback;
			return this;
		}
		
		public Builder setTitleBackgroundColor(int color) {
			arguments.putInt(PickerDialog.KEY_COLOR, color);
			return this;
		}
		
		public Builder setTitle(CharSequence title) {
			arguments.putCharSequence(PickerDialog.KEY_TITLE, title);
			return this;
		}
		
		public void show() {
			PickerDialog settingsDialog = new PickerDialog();
			settingsDialog.setOnPictureSelectedCallback(new OnPictureSelected() {
				
				@Override
				public void onSelected(String id) {
					mCallback.execute(id);
					
				}
			});
			settingsDialog.setArguments(arguments);
			settingsDialog.show(mActivity.getFragmentManager(), "tag");
			mActivity.getFragmentManager().executePendingTransactions();
		}
	}
}
