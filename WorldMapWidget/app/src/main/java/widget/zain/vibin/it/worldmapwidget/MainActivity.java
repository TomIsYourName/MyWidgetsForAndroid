package widget.zain.vibin.it.worldmapwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Random;

import widget.zain.vibin.it.worldmapwidget.widget.CustomMark;
import widget.zain.vibin.it.worldmapwidget.widget.WorldMapView;

public class MainActivity extends AppCompatActivity implements WorldMapView.OnMarkClickListener {

    private WorldMapView worldMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        worldMapView = (WorldMapView) findViewById(R.id.wmv);

        worldMapView.addMark(new CustomMark(R.drawable.mark, 120.0, 30.0));// Shanghai, China

        worldMapView.addMark(new CustomMark(R.drawable.mark, -120.0, 30.0));// US

        worldMapView.setOnMarkClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            worldMapView.addMark(new CustomMark(R.drawable.mark, new Random().nextDouble() * 180.0, new Random().nextDouble() * 90.0));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMarkClicked(int markId) {
        Log.d("mt", "clicked id is >>> " + markId);
    }
}
