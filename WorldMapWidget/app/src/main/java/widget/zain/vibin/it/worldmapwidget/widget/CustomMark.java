package widget.zain.vibin.it.worldmapwidget.widget;

import java.util.Random;

/**
 * Created by zain on 15-7-9.
 */
public class CustomMark {

    public int drawable;
    public double lat, lng;
    public int id;

    public CustomMark(int drawable, double lat, double lng) {
        this.drawable = drawable;
        this.lat = lat;
        this.lng = lng;
        this.id = new Random().nextInt(100);
    }

    public CustomMark(int id, int drawable, double lat, double lng) {
        this(drawable, lat, lng);
        this.id = id;
    }

}
