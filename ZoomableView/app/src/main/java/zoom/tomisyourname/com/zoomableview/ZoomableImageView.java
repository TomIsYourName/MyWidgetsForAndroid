package zoom.tomisyourname.com.zoomableview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
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
    private float initScale, midScale, maxScale;
    private ScaleGestureDetector mScaleGestureDetector;

    private int mLastPointCount;
    private float mLastX, mLastY;
    private boolean mDragable;
    private int mScaledTouchSlop;
    private GestureDetector mGestureDetector;

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        mMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        setOnTouchListener(this);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float scale = getScale();
                float targetScale = initScale;
                float[] scalePoint = new float[]{e.getX(), e.getY()};
                if(scale < midScale) {// scale to midScale
                    targetScale = midScale;
                }
                postDelayed(new SmoothScaleRunnable(scale, targetScale, scalePoint), 10);
                return true;
            }
        });
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

    private float getScale() {
        return getXScale();
    }

    private RectF getDrawableRect() {// get the rect of the current drawable
        Drawable d = getDrawable();
        if(d == null) return null;
        RectF rect = new RectF();
        rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        mMatrix.mapRect(rect);
        return rect;
    }

    private void borderCheck() {
        RectF rect = getDrawableRect();
        if(rect == null) return;

        float dx = 0;
        float dy = 0;
        int width = getWidth();
        int height = getHeight();

        if(rect.width() >= width) {// need to check horizontal borders
            if(rect.left > 0) dx = -rect.left;
            if(rect.right < width) dx = width - rect.right;
        }
        if(rect.height() >= height) {// need to check vertical borders
            if(rect.top > 0) dy = -rect.top;
            if(rect.bottom < height) dy = height - rect.bottom;
        }
        if(rect.width() < width) {
            dx = width / 2f - rect.right + rect.width() / 2f;
        }
        if(rect.height() < height) {
            dy = height / 2f - rect.bottom + rect.height() / 2f;
        }
        mMatrix.postTranslate(dx, dy);
    }

    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mScaledTouchSlop;
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

        mLastX = width * 1.0f / 2;
        mLastY = height * 1.0f / 2;

        mMatrix.postTranslate(dx, dy);
        mMatrix.postScale(initScale, initScale, mLastX, mLastY);
        setImageMatrix(mMatrix);

        mInitialed = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(mGestureDetector.onTouchEvent(event)) return true;

        mScaleGestureDetector.onTouchEvent(event);

        int pointCount = event.getPointerCount();

        float x = 0;
        float y = 0;

        for(int i=0; i < pointCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= pointCount * 1.0f;
        y /= pointCount * 1.0f;

        if(mLastPointCount != pointCount) {
            mDragable = false;
            mLastX = x;
            mLastY = y;
        }

        mLastPointCount = pointCount;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // can move
                float dx = x - mLastX;
                float dy = y - mLastY;

                if(!mDragable) mDragable = isMoveAction(dx, dy);

                if(mDragable) {
                    RectF rect = getDrawableRect();
                    if(rect == null) return false;

                    // no need to move on X
                    if(rect.width() < getWidth()) dx = 0;
                    // no need to move on Y
                    if(rect.height() < getHeight()) dy = 0;

                    mMatrix.postTranslate(dx, dy);
                    borderCheck();
                    setImageMatrix(mMatrix);
                    mLastX = x;
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastPointCount = 0;
                break;
        }
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Drawable d = getDrawable();
        if(d == null) return true;

        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if((scale < maxScale && scaleFactor > 1.0f) || (scale > initScale && scaleFactor < 1.0f)) {
            if(scale * scaleFactor > maxScale) {
                scaleFactor = maxScale / scale;
            }
            if(scale * scaleFactor < initScale) {
                scaleFactor = initScale / scale;
            }
            mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            borderCheck();
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
        // nothing need to do here
    }

    class SmoothScaleRunnable implements Runnable {

        private static final float BIGGER = 1.04f;
        private static final float SMALLER = 0.96f;
        private float currentScale;
        private float targetScale;
        private float[] scalePoint;
        private float scale;

        public SmoothScaleRunnable(float currentScale, float targetScale, float[] scalePoint) {
            this.currentScale = currentScale;
            this.targetScale = targetScale;
            this.scalePoint = scalePoint;
            if(currentScale < targetScale) {
                scale = BIGGER;
            } else {
                scale = SMALLER;
            }
        }

        @Override
        public void run() {
            mMatrix.postScale(scale, scale, scalePoint[0], scalePoint[1]);
            borderCheck();
            setImageMatrix(mMatrix);
            currentScale = getScale();

            if((scale > 1.0f && currentScale < targetScale) ||
                    (scale < 1.0f && currentScale > targetScale)) {
                postDelayed(this, 10);
            } else {
                mMatrix.postScale(
                        targetScale / currentScale, targetScale / currentScale,
                        scalePoint[0], scalePoint[1]);
                borderCheck();
                setImageMatrix(mMatrix);
            }
        }
    }
}
