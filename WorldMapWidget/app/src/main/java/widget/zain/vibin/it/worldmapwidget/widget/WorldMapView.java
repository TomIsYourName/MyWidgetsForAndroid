package widget.zain.vibin.it.worldmapwidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import widget.zain.vibin.it.worldmapwidget.R;

/**
 *
 * A view that show a world map as background and show custom marks on it depends on the lat-lng of the mark
 *
 */
public class WorldMapView extends View {

    private static final String TAG = "WorldMapView";
    private Drawable mBackgroundMapDrawable;
    private List<MarkWrapper> mMarkWrappers;
    private int markWidth = 0;
    private int markHeight = 0;
    private OnMarkClickListener onMarkClickListener;
    private CustomMark clickedMark = null;

    public WorldMapView(Context context) {
        super(context);
        mMarkWrappers = new ArrayList<MarkWrapper>();
        init(null, 0);
    }

    public WorldMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMarkWrappers = new ArrayList<MarkWrapper>();
        init(attrs, 0);
    }

    public WorldMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mMarkWrappers = new ArrayList<MarkWrapper>();
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WorldMapView, defStyle, 0);

        if(a.hasValue(R.styleable.WorldMapView_background_map)) {
            mBackgroundMapDrawable = a.getDrawable(R.styleable.WorldMapView_background_map);
            mBackgroundMapDrawable.setCallback(this);
        }

        if(a.hasValue(R.styleable.WorldMapView_mark_width)) {
            markWidth = a.getInt(R.styleable.WorldMapView_mark_width, getResources().getDimensionPixelSize(R.dimen.mark_size));
        } else {
            markWidth = getResources().getDimensionPixelSize(R.dimen.mark_size);
        }

        if(a.hasValue(R.styleable.WorldMapView_mark_height)) {
            markHeight = a.getInt(R.styleable.WorldMapView_mark_height, getResources().getDimensionPixelSize(R.dimen.mark_size));
        } else {
            markHeight = getResources().getDimensionPixelSize(R.dimen.mark_size);
        }

        a.recycle();
    }

    private CustomMark getClickedMark(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if(onMarkClickListener!= null && mMarkWrappers != null) {
            for(MarkWrapper wrapper : mMarkWrappers) {
                if((x > wrapper.left && x < wrapper.right) && (y > wrapper.top && y < wrapper.bottom)) {
                    return wrapper.realMark;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouch >>>" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clickedMark = getClickedMark(event);
                if(clickedMark != null) return true;
                break;
            case MotionEvent.ACTION_UP:
                if(clickedMark != null) {
                    onMarkClickListener.onMarkClicked(clickedMark.id);
                    Log.d(TAG, "clicked a mark with id >>> " + clickedMark.id);
                } else {
                    Log.d(TAG, "clicked a none mark area");
                }
                clickedMark = null;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the example drawable on top of the text.
        if (mBackgroundMapDrawable != null) {
            mBackgroundMapDrawable.setBounds(0, 0, contentWidth, contentHeight);
            mBackgroundMapDrawable.draw(canvas);
        }

        if(mMarkWrappers != null) {
            for(MarkWrapper wrapper : mMarkWrappers) {
                if(wrapper == null) return;
                wrapper.fixBounds(getWidth(), getHeight());
                Drawable markDrable = getResources().getDrawable(wrapper.realMark.drawable);
                if(markDrable != null) {
                    markDrable.setBounds(wrapper.left, wrapper.top, wrapper.right, wrapper.bottom);
                    markDrable.setCallback(this);
                    markDrable.draw(canvas);
                }
            }
        }
    }

    /**
     * Gets the background drawable attribute value.
     *
     * @return The background drawable attribute value.
     */
    public Drawable getmBackgroundMapDrawable() {
        return mBackgroundMapDrawable;
    }

    /**
     * Sets the view's background drawable attribute value.
     *
     * @param backgroundDrawable The background drawable attribute value to use.
     */
    public void setmBackgroundMapDrawable(Drawable backgroundDrawable) {
        mBackgroundMapDrawable = backgroundDrawable;
        invalidate();
    }

    public void addMark(CustomMark mark) {
        if(mark != null) {
            mMarkWrappers.add(new MarkWrapper(mark));
            invalidate();
        }
    }

    public void setOnMarkClickListener(OnMarkClickListener onMarkClickListener) {
        this.onMarkClickListener = onMarkClickListener;
    }

    private class MarkWrapper {

        public CustomMark realMark;
        public int left, top, right, bottom;

        public MarkWrapper(CustomMark mark) {
            realMark = mark;
        }

        public void fixBounds(int mapWidth, int mapHeight) {
            left = getLeft(realMark, mapWidth);
            top = getTop(realMark, mapHeight);
            right = left + markWidth;
            bottom = top + markHeight;
            Log.d(TAG, String.format("(%d, %d, %d, %d)", left, top, right, bottom));
        }

        private int getTop(CustomMark mark, int mapHeight) {
            double centerY = mapHeight * (90 - mark.lng) / 180.0;
            return (int) (centerY - markHeight / 2);
        }

        private int getLeft(CustomMark mark, int mapWidth) {
            double centerX = mapWidth * (mark.lat + 180) / 360.0;
            return (int) (centerX - markWidth / 2);
        }
    }

    public interface OnMarkClickListener {
        public void onMarkClicked(int markId);
    }
}
