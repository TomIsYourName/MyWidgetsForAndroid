package gridview.tomisyourname.com.gridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

/**
 *
 * @author Zain Zhu<zain.zhu@vibin.it, tomisyourname@gmail.com>
 *
 */
public class VibinGridView extends GridView implements AbsListView.OnScrollListener {

    private static final int MAX_OVERSCROLL_Y = 2;
    private VibinScrollListener vibinScrollListener;
    private int lastScrolledY = 0;

    public VibinGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollListener(this);
    }

    public VibinGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VibinGridView(Context context) {
        this(context, null);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if(vibinScrollListener != null){
            // use the over scroll to detect scroll to top/bottom (wakaka, AM I SMART? :D )
            if(scrollY < 0) vibinScrollListener.onScrollToTop();
            if(scrollY > 0) vibinScrollListener.onScrollToBottom();
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                   int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, MAX_OVERSCROLL_Y, isTouchEvent);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE && vibinScrollListener != null) {
            vibinScrollListener.onScrollStopped();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int currentScrollY = getScrollOnY();
        if(lastScrolledY < 2) {
            lastScrolledY = currentScrollY;
            return;
        }
        if(currentScrollY > lastScrolledY) {
            // scroll up
            if(vibinScrollListener != null) {
                vibinScrollListener.onScrolling(VibinScrollListener.SCROLL_DIRECTION_UP, currentScrollY);
            }
        } else if(currentScrollY < lastScrolledY) {
            // scroll down
            if(vibinScrollListener != null) {
                vibinScrollListener.onScrolling(VibinScrollListener.SCROLL_DIRECTION_DOWN, currentScrollY);
            }
        }
        lastScrolledY = currentScrollY;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private int getScrollOnY() {// get the scroll position on Y
        View c = getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight() ;
    }

    public void setScrollListener(VibinScrollListener vibinScrollListener) {
        this.vibinScrollListener = vibinScrollListener;
    }

    public interface VibinScrollListener {

        public static final int SCROLL_DIRECTION_UP = 0;
        public static final int SCROLL_DIRECTION_DOWN = 1;

        public void onScrolling(int direction, int scrollY);
        public void onScrollToTop();
        public void onScrollToBottom();
        public void onScrollStopped();

    }
}

