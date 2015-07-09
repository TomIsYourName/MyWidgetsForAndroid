package widget.zain.vibin.it.worldmapwidget.widget;

/**
 * Created by zain on 15-7-9.
 */
public class CustomMark {

    public int drawable;
    public double lat, lng;

    public CustomMark(int drawable, double lat, double lng) {
        this.drawable = drawable;
        this.lat = lat;
        this.lng = lng;
    }
}
