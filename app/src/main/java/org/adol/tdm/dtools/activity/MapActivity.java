package org.adol.tdm.dtools.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.util.svgmap.SVGMapController;
import com.util.svgmap.SVGMapView;
import com.util.svgmap.SVGMapViewListener;
import com.util.svgmap.core.data.SVGPicture;
import com.util.svgmap.core.helper.ImageHelper;
import com.util.svgmap.core.helper.map.SVGBuilder;
import com.util.svgmap.sample.helper.AssetsHelper;

import org.adol.tdm.dtools.R;
import org.adol.tdm.dtools.overlay.BitmapOverlay;

public class MapActivity extends ActionBarActivity implements SVGMapController.MarkPointListener {

    private SVGMapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mapView = (SVGMapView) findViewById(R.id.center_mapview);

        mapView.registerMapViewListener(new SVGMapViewListener()
        {
            @Override
            public void onMapLoadComplete()
            {
                MapActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(MapActivity.this, "onMapLoadComplete", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onMapLoadError()
            {
                MapActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(MapActivity.this, "onMapLoadError", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onGetCurrentMap(Bitmap bitmap)
            {
                MapActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(MapActivity.this, "onGetCurrentMap", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        mapView.setBrandBitmap(ImageHelper.drawableToBitmap(new SVGBuilder().readFromString(SVGPicture.LOGO).build().getDrawable(), 1.0f));
        mapView.setCenterPoint(ImageHelper.drawableToBitmap(new SVGBuilder().readFromString(SVGPicture.THUMBTACK).build().getDrawable(), 1.0f));
        mapView.loadMap(AssetsHelper.getContent(this, "sample2.svg"));
        mapView.registerMarkPointListener(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void mark(float x, float y) {
        float[] mapCoordinate = mapView.getMapCoordinateWithScreenCoordinate(x, y);
        BitmapOverlay locationOverlay = new BitmapOverlay(mapView, 30, 30, mapCoordinate[0], mapCoordinate[1], "Hello World\nYaoJiehong");
        mapView.getOverLays().add(locationOverlay);
        mapView.refresh();
    }
}
