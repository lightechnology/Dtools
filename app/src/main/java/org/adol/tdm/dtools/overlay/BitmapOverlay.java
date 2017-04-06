package org.adol.tdm.dtools.overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.Toast;

import com.util.svgmap.SVGMapView;
import com.util.svgmap.core.data.SVGPicture;
import com.util.svgmap.core.helper.ImageHelper;
import com.util.svgmap.core.helper.map.SVGBuilder;
import com.util.svgmap.overlay.SVGMapBaseOverlay;

/**
 * Created by adolp on 2017/4/6.
 */

public class BitmapOverlay extends SVGMapBaseOverlay {

    private SVGMapView svgMapView;
    private String info;
    private Bitmap bitmap;
    private float sc;

    public BitmapOverlay(SVGMapView svgMapView, float x, float y, String info) {
        init(svgMapView, -1, -1, x, y, info);
    }

    public BitmapOverlay(SVGMapView svgMapView, float width, float height, float x, float y, String info) {
        init(svgMapView, width, height, x, y, info);
    }

    public void init(SVGMapView svgMapView, float width, float height, float x, float y, String info) {
        this.x = x;
        this.y = y;
        this.info = info;
        this.svgMapView = svgMapView;
        this.bitmap = ImageHelper.drawableToBitmap(new SVGBuilder().readFromString(SVGPicture.MARK_POINT).build().getDrawable(), 1.0f);
        this.sc = 1.0f;
        if (0 < width && 0 < height)
            if (width < height) {
                if (bitmap.getWidth() < bitmap.getHeight()) {
                    sc = bitmap.getWidth() / width;
                } else {
                    sc = bitmap.getWidth() / width;
                }
            } else {
                if (bitmap.getWidth() < bitmap.getHeight()) {
                    sc = bitmap.getWidth() / height;
                } else {
                    sc = bitmap.getWidth() / height;
                }
            }
        this.width = bitmap.getWidth() / sc;
        this.height = bitmap.getHeight() / sc;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onTap(MotionEvent event) {
        float[] mapCoordinate = svgMapView.getMapCoordinateWithScreenCoordinate(event.getX(), event.getY());
        if (mapCoordinate[0] + width / 2 >= x && mapCoordinate[0] <= x + width / 2 && mapCoordinate[1] + height / 2 >= y && mapCoordinate[1] <= y + height / 2) {
            Toast.makeText(svgMapView.getContext(), null == info ? "" : info, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLongTap(MotionEvent event) {
        float[] mapCoordinate = svgMapView.getMapCoordinateWithScreenCoordinate(event.getX(), event.getY());
        if (mapCoordinate[0] + width / 2 >= x && mapCoordinate[0] <= x + width / 2 && mapCoordinate[1] + height / 2 >= y && mapCoordinate[1] <= y + height / 2) {
            Toast.makeText(svgMapView.getContext(), "Long Tap", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix, float currentZoom, float currentRotateDegrees) {
        canvas.save();
        canvas.setMatrix(matrix);
        Matrix matrixt = new Matrix();
        matrixt.setValues(new float[]{1, 0, x * sc - bitmap.getWidth() / 2, 0, 1, y * sc - bitmap.getHeight() / 2, 0, 0, sc});
        canvas.drawBitmap(bitmap, matrixt, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.restore();
    }

    @Override
    public Integer[] getWH() {
        return new Integer[0];
    }
}
