package fr.vadimcaen.facebookphotopicker.GraphInterface;

import com.facebook.model.GraphObject;

public interface GraphPicture extends GraphObject {

	String getSource();
	
	String getPicture();
	
	String getId();
}
