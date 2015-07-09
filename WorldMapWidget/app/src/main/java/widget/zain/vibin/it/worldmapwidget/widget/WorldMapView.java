package widget.zain.vibin.it.worldmapwidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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

    private Drawable mBackgroundMapDrawable;
    private List<CustomMark> mCustomMarks;
    private int markWidth = 0;
    private int markHeight = 0;

    public WorldMapView(Context context) {
        super(context);
        mCustomMarks = new ArrayList<CustomMark>();
        init(null, 0);
    }

    public WorldMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCustomMarks = new ArrayList<CustomMark>();
        init(attrs, 0);
    }

    public WorldMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mCustomMarks = new ArrayList<CustomMark>();
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

    private int getTop(CustomMark mark) {
        double centerY = getHeight() * (90 - mark.lng) / 180.0;
        return (int) (centerY - markHeight / 2);
    }

    private int getLeft(CustomMark mark) {
        double centerX = getWidth() * (mark.lat + 180) / 360.0;
        return (int) (centerX - markWidth / 2);
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

        if(mCustomMarks != null) {
            for(CustomMark mark : mCustomMarks) {
                Drawable markDrable = getResources().getDrawable(mark.drawable);
                if(markDrable != null) {
                    int left = getLeft(mark);
                    int top = getTop(mark);
                    markDrable.setBounds(left, top, left + markWidth, top + markHeight);
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
            mCustomMarks.add(mark);
            invalidate();
        }
    }
}
