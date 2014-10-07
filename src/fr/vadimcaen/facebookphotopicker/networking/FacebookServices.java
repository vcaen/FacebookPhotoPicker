package fr.vadimcaen.facebookphotopicker.networking;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.LangUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;

import fr.vadimcaen.facebookphotopicker.GraphInterface.Album;
import fr.vadimcaen.facebookphotopicker.GraphInterface.GraphPicture;

public class FacebookServices {

	static final String PATH_ME     = "/me";
	static final String PATH_ALBUM  = "/albums";
	static final String PATH_PHOTOS = "/photos";

	static final String PARAM_FIELD = "fields";
	static final String PARAM_LIMIT = "limit";

	static final String FIELD_ALBUM_COVER = "name,cover_photo";
	static final String FIELD_ALBUM = "name";
	static final String FIELD_PHOTOS = "source";
	static final String FIELD_PICTURE_SOURCE = "source,picture";

	public static final String PROPERTY_SOURCE  = "source";
	public static final String PROPERTY_NAME    = "name";
	public static final String PROPERTY_PICTURE = "picture";
	public static final String PROPERTY_ID      = "id";
	public static final String PROPERTY_DATA    = "data";

	static final String ALBUM_PROFILE_NAME = "Profile Pictures";

	static Session session = Session.getActiveSession();

	public static void getProfilePictures(final int number, final NetworkCallback<GraphPicture[]> lCallback) {
		Bundle param = new Bundle();
		param.putString(PARAM_FIELD, FIELD_ALBUM);

		// * TODO TEST
		Request request = new Request(session, PATH_ME + PATH_ALBUM, param,
				HttpMethod.GET, new Callback() {

					@Override
					public void onCompleted(Response response) {

						// Fist fetch the id of the profile picture album
						String albumName = "";
						String albumId = "";

						// Parse all the album to find the profile picture album
						GraphObjectList<GraphObject> list = response
								.getGraphObject().getPropertyAsList("data",
										GraphObject.class);
						for (GraphObject obj : list) {
							albumName = (String) obj.getProperty(PROPERTY_NAME);
							if (albumName.equalsIgnoreCase(ALBUM_PROFILE_NAME)) {

								// Retrieving the id of the album
								albumId = (String) obj.getProperty(PROPERTY_ID);
								break;
							}
						}

						if (!albumId.isEmpty()) {
							Bundle subparam = new Bundle();
							subparam.putString(PARAM_FIELD, FIELD_PICTURE_SOURCE);
							subparam.putInt(PARAM_LIMIT, number);
							// Secondly get the url of the profile pictures
							Request subrequest = new Request(session, albumId
									+ PATH_PHOTOS, subparam, HttpMethod.GET,
									new Callback() {

										@Override
										public void onCompleted(Response response) {

											ArrayList<FacebookPicture> urlList = new ArrayList<FacebookPicture>();

											GraphObjectList<GraphPicture> list = response
													.getGraphObject()
													.getPropertyAsList(PROPERTY_DATA, GraphPicture.class);

											// Calling the callback
											lCallback.execute(list.toArray(
													new GraphPicture[urlList.size()]));
										}
									});
							subrequest.executeAsync();
						}
					}
				});
		request.executeAsync();
		// */
	}

	public void getPictureById(String id, final NetworkCallback<GraphPicture> callback) {
		Bundle param = new Bundle();
		param.putString(PARAM_FIELD, FIELD_PICTURE_SOURCE);

		Request request = new Request(session, "/" + id, param, HttpMethod.GET,new Callback() {

			@Override
			public void onCompleted(Response response) {
				GraphPicture graphPicture = response.getGraphObjectAs(GraphPicture.class);
				callback.execute(graphPicture);
			}
		});
		request.executeAsync();
	}
	
	public void getPicturesOfAlbum(String albumid, final NetworkCallback<GraphObjectList<GraphPicture>> callback) {
		Bundle subparam = new Bundle();
		subparam.putString(PARAM_FIELD, FIELD_PICTURE_SOURCE);
		//subparam.putInt(PARAM_LIMIT, number);
		// Secondly get the url of the profile pictures
		Request request = new Request(session, albumid
				+ PATH_PHOTOS, subparam, HttpMethod.GET,
				new Callback() {
					@Override
					public void onCompleted(Response response) {

						GraphObjectList<GraphPicture> list = response
								.getGraphObject()
								.getPropertyAsList(PROPERTY_DATA, GraphPicture.class);

						// Calling the callback
						callback.execute(list);
					}
				});
		request.executeAsync();
	}

	public void getPicturePropertiesById(String id,
			final NetworkCallback<GraphObject> lCallback) {
		Bundle param = new Bundle();
		param.putString(PARAM_FIELD, FIELD_PICTURE_SOURCE);
		Request request = new Request(session, "/" + id, param, HttpMethod.GET,
				new Callback() {

					@Override
					public void onCompleted(Response response) {

						lCallback.execute(response.getGraphObject());
					}
				});
		request.executeAsync();
	}

	public void getAlbums(final NetworkCallback<GraphObjectList<Album>> callback) {
		Bundle param = new Bundle();
		param.putString(PARAM_FIELD, FIELD_ALBUM_COVER);

		Request request = new Request(session, PATH_ME + PATH_ALBUM, param,
				HttpMethod.GET, new Callback() {

					@Override
					public void onCompleted(Response response) {

						JSONArray albums = (JSONArray) response
								.getGraphObject().getProperty(PROPERTY_DATA);

						GraphObjectList<Album> graphAlbums = GraphObject.Factory
								.createList(albums, Album.class);
						callback.execute(graphAlbums);
					}
				});
		request.executeAsync();
	}

}
