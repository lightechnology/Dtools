package com.util.svgmap.core.componet;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.util.svgmap.overlay.SVGMapBaseOverlay;


public class MapOverlay extends SVGMapBaseOverlay {

    private MapMainView mapMainView;
    private Picture floorMap;
    private boolean hasMeasured;

    private static final String TAG = "MapLayer";

    public MapOverlay(MapMainView mapMainView) {
        this.mapMainView = mapMainView;
        this.showLevel = MAP_LEVEL;
    }

    public void setData(Picture floorMap) {
        this.floorMap = floorMap;
        if (this.mapMainView.getWidth() == 0) {
            ViewTreeObserver vto = this.mapMainView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (!hasMeasured) {
                        calcRatio();
                    }
                    return true;
                }
            });
        } else {
            calcRatio();
        }
    }

    public Picture getFloorMap() {
        return this.floorMap;
    }

    private void calcRatio() {
        this.mapMainView.showCenterThumbtack();
        float zoom = getInitScale(this.mapMainView.getWidth(), this.mapMainView.getHeight(), floorMap.getWidth(), floorMap.getHeight());
        Log.i(TAG, zoom + " = zoom");
        this.mapMainView.setMinScale(zoom);
        this.mapMainView.setCurrentScale(zoom, 0, 0);
        float deltaHeight = this.mapMainView.getHeight() - zoom * floorMap.getHeight();
        float deltaWidth = this.mapMainView.getWidth() - zoom * floorMap.getWidth();
        this.mapMainView.translateBy(deltaWidth / 2, deltaHeight / 2);
        this.mapMainView.refresh();
        hasMeasured = true;
    }

    private float getInitScale(float width, float height, float imageWidth, float imageHeight) {
        float widthRatio = width / imageWidth;
        float heightRatio = height / imageHeight;
        if (widthRatio * imageHeight <= height) {
            return widthRatio;
        } else if (heightRatio * imageWidth <= width) {
            return heightRatio;
        }
        return 0;
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onTap(MotionEvent event) {
    }

    @Override
    public void onLongTap(MotionEvent event) {
    }

    @Override
    public void onDestroy() {
        this.floorMap = null;
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix, float currentZoom, float currentRotateDegrees) {
        canvas.save();
        canvas.setMatrix(matrix);
        if (floorMap != null) {
            canvas.drawPicture(floorMap);
        }
        canvas.restore();
    }

    @Override
    public Integer[] getWH() {
        Integer[] wh = {0, 0};
        if (floorMap != null) {
            wh[0] = floorMap.getWidth();
            wh[1] = floorMap.getHeight();
        }
        return wh;
    }
}
