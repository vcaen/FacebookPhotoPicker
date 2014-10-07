package fr.vadimcaen.facebookphotopicker.networking;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.model.GraphObjectList;

import fr.vadimcaen.facebookphotopicker.R;
import fr.vadimcaen.facebookphotopicker.GraphInterface.Album;
import fr.vadimcaen.facebookphotopicker.GraphInterface.GraphPicture;
import fr.vadimcaen.facebookphotopicker.widget.PickerElemView;

public class PickerDialog extends DialogFragment {

	private static final String TAG = "PickerDialog";
	private static final int ELEM_PER_ROW    = 3;
	private static final int ELEM_HEIGHT_DP  = 200;
	private static final int ROW_MARGIN_DP   = 10;
	private static final int ELEM_MARGIN_DP  = 5;
	private static final FacebookServices facebookServices = new FacebookServices();
	
	public static final String KEY_COLOR = "color";
	public static final String KEY_TITLE = "title";
	
	private int titleBarColor;
	private String title;
	
	private FacebookPhotoPicker caller;


	// private Context mContext;

	int elemNumber = 0;
	TableRow currentTableRow;
	LinearLayout pickerAlbumTable;
	LinearLayout pickerPhotoTable;
	Button pickerNavigationButton;
	TextView titleTextView;
	RelativeLayout pickerTopBar;
	
	ArrayList<PickerElemView> savedView = new ArrayList<PickerElemView>();
	OnPictureSelected mCallback;


	public PickerDialog() {
		// Empty constructor required for DialogFragment
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout to use as dialog or embedded fragment
		View view = inflater.inflate(R.layout.picker_dialog, container, false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		titleTextView = (TextView) view.findViewById(R.id.picker_title);
		pickerTopBar = (RelativeLayout) view.findViewById(R.id.picker_top_bar);
		pickerAlbumTable = (LinearLayout) view.findViewById(R.id.picker_table);
		pickerNavigationButton = (Button) view.findViewById(R.id.picker_navigation_button);
		pickerNavigationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				displayAlbums();
			}
		});
		
		Bundle args = getArguments();
		if(args.containsKey(KEY_TITLE)) {
			titleTextView.setText(args.getCharSequence(KEY_TITLE));
		}
		if(args.containsKey(KEY_COLOR)) {
			pickerTopBar.setBackgroundColor(args.getInt(KEY_COLOR));
		}
		
		displayAlbums();
		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		return dialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog d = getDialog();

		// Set the dialog with full width
		if (d != null) {
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.WRAP_CONTENT;
			d.getWindow().setLayout(width, height);
		}
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
	}
	

	private void displayAlbums() {
		elemNumber = 0;
		pickerAlbumTable.removeAllViews();
		pickerNavigationButton.setVisibility(View.GONE);
		currentTableRow = null;
		if(savedView.isEmpty()) {
			facebookServices
			.getAlbums(new NetworkCallback<GraphObjectList<Album>>() {
	
				@Override
				public void execute(GraphObjectList<Album> object) {
					if(getActivity() != null) 
						for (Album album : object) {
							if (elemNumber % ELEM_PER_ROW == 0) {
								currentTableRow = createNewTableRow();
								pickerAlbumTable.addView(currentTableRow);
							}
							final PickerElemView view;
							view = new PickerElemView(getActivity());
							view.setLayoutParams(getElemLayout());
							view.setText(album.getName());
							view.setTag(album.getId());
							view.setOnTouchListener(new OnAlbumTouchListener());
							savedView.add(view);
							currentTableRow.addView(view);
							
							String coverPhotoId = album.getCoverPhoto();
							if(coverPhotoId != null) {
								facebookServices.getPictureById(
										coverPhotoId, 
										new NetworkCallback<GraphPicture>() {
			
									@Override
									public void execute(GraphPicture object) {
										FacebookPicture facebookPicture = new FacebookPicture(object, getActivity());
										facebookPicture.getBitmap(new NetworkCallback<Bitmap>() {
			
											@Override
											public void execute(Bitmap object) {
												if(getActivity() != null) {
													view.setImage(object);
												}
											}
										});
									}
								});
							} else {
								view.setImage(BitmapFactory.decodeResource(getResources(),R.drawable.empty_album));
							}
							elemNumber++;
						}
				}
				
			});
		} else {
			
			for (PickerElemView view : savedView) {
				ViewGroup parent = (ViewGroup) view.getParent();
				if(parent != null) {
					if(pickerAlbumTable.indexOfChild(parent) < 0)
						pickerAlbumTable.addView(parent);
					currentTableRow = (TableRow) parent;
					
				} else {
					if (elemNumber % ELEM_PER_ROW == 0) {
						currentTableRow = createNewTableRow();
						pickerAlbumTable.addView(currentTableRow);
					}
					currentTableRow.addView(view);
				}
				elemNumber++;
			}
		}
	}
	
	private void displayPicturesForAlbum(String albumid) {
		pickerAlbumTable.removeAllViews();
		pickerNavigationButton.setVisibility(View.VISIBLE);
		elemNumber = 0;
		facebookServices.getPicturesOfAlbum(albumid, new NetworkCallback<GraphObjectList<GraphPicture>>() {
			
			@Override
			public void execute(GraphObjectList<GraphPicture> object) {
				for(GraphPicture graphPicture : object) {
					if (elemNumber % ELEM_PER_ROW == 0) {
						currentTableRow = createNewTableRow();
						pickerAlbumTable.addView(currentTableRow);
					}
					FacebookPicture facebookPicture = new FacebookPicture(graphPicture, getActivity());
					final PickerElemView view;
					view = new PickerElemView(getActivity());
					view.setLayoutParams(getElemLayout());
					view.setTag(graphPicture.getId());
					view.setOnTouchListener(new OnPictureTouchedListener());
					currentTableRow.addView(view);
					elemNumber++;
					facebookPicture.getBitmap(new NetworkCallback<Bitmap>() {

						@Override
						public void execute(Bitmap object) {
							view.setImage(object);
						}
					});
				}
			}
		});
	}
	
	
	private TableRow createNewTableRow() {
		
		TableRow row = new TableRow(getActivity());
		row.setWeightSum(ELEM_PER_ROW);
		LinearLayout.LayoutParams params = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		params.bottomMargin = dpToPx(ROW_MARGIN_DP).intValue();
		row.setLayoutParams(params);
		
		return row;
	}


	private TableRow.LayoutParams getElemLayout() {
		TableRow.LayoutParams params =  new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,
				1);
		params.leftMargin = dpToPx(ELEM_MARGIN_DP).intValue();
		params.rightMargin = dpToPx(ELEM_MARGIN_DP).intValue();
		return params;
	}
	
	private Float dpToPx(int dp) {
		Resources r = getResources();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}
	
	public void setOnPictureSelectedCallback(OnPictureSelected callback) {
		mCallback = callback;
	}
	
	private class OnAlbumTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				return true;
			case MotionEvent.ACTION_UP:
				displayPicturesForAlbum((String) v.getTag()); 
				break;

			default:
				break;
			}
			return false;
		}
	}
	
	private class OnPictureTouchedListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
						
				return true;
			case MotionEvent.ACTION_UP:
				if(mCallback != null) {
					mCallback.onSelected((String) v.getTag()); 
				}
				dismiss();
				return true;

			default:
				break;
			}
			return false;
		}
		
	}
	
	public interface OnPictureSelected {
		public void onSelected(String id);
	}
}
