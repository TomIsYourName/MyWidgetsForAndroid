package gridview.tomisyourname.com.gridview;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class MainActivity extends ActionBarActivity implements VibinGridView.VibinScrollListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VibinGridView girdView = (VibinGridView) findViewById(R.id.vgv);
        girdView.setAdapter(new TestAdapter());
        girdView.setScrollListener(this);
    }

    @Override
    public void onScrolling(int direction, int scrollY) {
        Log.d("test", "scrolling...");
    }

    @Override
    public void onScrollToTop() {
        Log.d("test", ">>>scroll to top!<<<");
    }

    @Override
    public void onScrollToBottom() {
        Log.d("test", ">>>scroll to bottom!<<<");
    }

    @Override
    public void onScrollStopped() {
        Log.d("test", ">>>scroll stopped!<<<");
    }

    class TestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 300;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, null);
            }
            return convertView;
        }
    }
}
