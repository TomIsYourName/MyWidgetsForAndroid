package zoom.tomisyourname.com.zoomableview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * @author Zain Zhu<zain.zhu@vibin.it, tomisyourname@gmail.com>
 *
 */
public class ZoomableImageView extends ImageView implements
        ViewTreeObserver.OnGlobalLayoutListener,
        View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private boolean mInitialed = false;
    private Matrix mMatrix;
    private float initScale;
    private float midScale;
    private float maxScale;
    private ScaleGestureDetector mScaleGestureDetector;

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        mMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        setOnTouchListener(this);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableImageView(Context context) {
        this(context, null);
    }

    private float getXScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    private float getYScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_Y];
    }

    @Override
    public void onGlobalLayout() {// here we scale the drawable to fit screen

        if(mInitialed) return;

        Drawable d = getDrawable();
        if(d == null) return;

        int width = getWidth();// get the widget width
        int height = getHeight();// get the widget height
        int dw = d.getIntrinsicWidth();// get the drawable width
        int dh = d.getIntrinsicHeight();// get the drawable height

        initScale = 1.0f;
        if((dw > width && dh > height) || (dw < width && dh < height)) {
            initScale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
        } else if(dw > width && dh < height) {
            initScale = width * 1.0f / dw;
        } else if(dw < width && dh > height) {
            initScale = height * 1.0f / dh;
        }

        midScale = initScale * 2;
        maxScale = initScale * 4;

        int dx = width / 2 - dw / 2;
        int dy = height / 2 - dh / 2;

        mMatrix.postTranslate(dx, dy);
        mMatrix.postScale(initScale, initScale, width / 2, height / 2);
        setImageMatrix(mMatrix);

        mInitialed = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Drawable d = getDrawable();
        if(d == null) return true;

        float scale = getXScale();
        float scaleFactor = detector.getScaleFactor();
        if((scale < maxScale && scaleFactor > 1.0f) || (scale > initScale && scaleFactor < 1.0f)) {
            if(scale * scaleFactor > maxScale) {
                scaleFactor = maxScale / scale;
            }
            if(scale * scaleFactor < initScale) {
                scaleFactor = initScale / scale;
            }
            mMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }
}
