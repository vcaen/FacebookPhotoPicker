package fr.vadimcaen.facebookphotopicker.widget;

import java.util.Random;

import fr.vadimcaen.facebookphotopicker.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PickerElemView extends RelativeLayout {

	private static final int    CURRENT_API_VERSION = android.os.Build.VERSION.SDK_INT;
	private static final long   REVEAL_DURATION = 200;
	private static final int    TEXT_SIZE = 10;
	private static final int    TEXT_BACKGROUND_ALPHA = 255;
	
	Bitmap defaultBitmap;
	
	private static final int[] COLORS  = {
		0xffffffff,
		0x55ffffff
	};
	
	Bitmap bitmap;
	CharSequence text;
	
	ImageView imageView;
	TextView textView;
	
	
	public PickerElemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public PickerElemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PickerElemView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		imageView = getImageView();
		defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty_album);
		addView(imageView);
	}
	
	private ImageView getImageView() {
		SquareImageView view;
		LayoutParams imagelayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imagelayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		imagelayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		imagelayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		imagelayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		view = new SquareImageView(getContext());
		view.setLayoutParams(imagelayoutParams);
		view.setImageBitmap(defaultBitmap);
		view.setScaleType(ScaleType.CENTER_CROP);
		return view;
	}
	
	private TextView getTextView() {
		TextView view;
		LayoutParams textLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		
		view = new TextView(getContext());
		view.setLayoutParams(textLayoutParams);
		view.setBackgroundDrawable(getTextBackground());
		view.setTextSize(TEXT_SIZE);
		view.setLines(2);
		view.setGravity(Gravity.CENTER);
		
		return view;
	}
	
	
	public void setText(CharSequence text) {
		this.text = text;
		
		// We display the background only if there is some text
		if(text.length() > 0) {
			if( textView == null) {
				textView = getTextView();
				addView(textView);
			}
			textView.setVisibility(VISIBLE);
			textView.setText(text);
		} else {
			if(textView != null) {
				textView.setVisibility(GONE);
			}
		}
	}
	
	public void setImage(Bitmap b) {
		imageView.setBackgroundColor(Color.BLUE);
		bitmap = b;
		//imageView.setAlpha(0f);
		imageView.setImageBitmap(bitmap);
		revealView(imageView);
		
		
	}
	
	@SuppressLint("NewApi")
	private void revealView(View v) {
		if (CURRENT_API_VERSION >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1){
			v.animate()
			.setDuration(REVEAL_DURATION)
			.alpha(1f)
			.start();
		} else {
			v.setAlpha(1f);
		}
		v.setBackgroundColor(Color.RED);
	}
	
	private Drawable getTextBackground() {
		GradientDrawable drawable = new GradientDrawable(Orientation.BOTTOM_TOP, COLORS );
		return drawable;
	}
}
