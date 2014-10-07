package fr.vadimcaen.facebookphotopicker.widget;

import android.graphics.Bitmap;

public class PickerElem {
	
	Bitmap bitmap;
	String name;
	
	public PickerElem(Bitmap bitmap, String name) {
		super();
		this.bitmap = bitmap;
		this.name = name;
	}

	public PickerElem(String name) {
		this.name = name;
	}
	
	public boolean hasBitmap() {
		return bitmap != null;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
