package fr.vadimcaen.facebookphotopicker.GraphInterface;

import com.facebook.model.GraphObject;

public interface Album extends GraphObject {
	
	String getId();
	
	String getLink();
	
	String getCoverPhoto();
	
	String getName();

}
