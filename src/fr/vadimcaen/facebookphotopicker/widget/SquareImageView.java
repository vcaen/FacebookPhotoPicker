package fr.vadimcaen.facebookphotopicker.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * View that display a square image.
 * 
 * @author vcaen
 * 
 */
public class SquareImageView extends ImageView {

	/**
	 * Enum of the side to use for the length of the square {@link ImageView}.
	 * 
	 * @author vcaen
	 * 
	 */
	public enum BaseSide {
		WIDTH, HEIGHT
	}

	private BaseSide baseSide = BaseSide.WIDTH;
	private Context context;

	public SquareImageView(Context c) {
		super(c);
		context = c;
		init();
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	
	private void init() {
		setScaleType(ScaleType.CENTER_CROP);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int size = (baseSide == BaseSide.WIDTH) ? getMeasuredWidth()
				: getMeasuredHeight();
		setMeasuredDimension(size, size);
	}
	

	/**
	 * Set which side of the image we should use for the length of the square's
	 * sides
	 * 
	 * @param bs
	 *            {@link BaseSide} Side of the base image to use as square's
	 *            sides' length
	 */
	public void setBaseSide(BaseSide bs) {
		baseSide = bs;
	}
}
