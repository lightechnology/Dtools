package com.util.svgmap;

import android.graphics.PointF;

import com.util.svgmap.core.componet.MapMainView;


public class SVGMapController {

    private MapMainView mapMainView;

    public SVGMapController(SVGMapView mapView) {
        this.mapMainView = (MapMainView) mapView.getChildAt(0);
    }

    public void setDragGestureEnabled(boolean enabled) {
        this.mapMainView.setDragGestureEnabled(enabled);
    }

    public void setZoomGestureEnabled(boolean enabled) {
        this.mapMainView.setZoomGestureEnabled(enabled);
    }

    public void setRotateGestureEnabled(boolean enabled) {
        if (!enabled) {
            setCurrentRotationDegrees(0);
        }
        this.mapMainView.setRotateGestureEnabled(enabled);
    }

    public void setZoomWithTouchEventCenterEnabled(boolean enabled) {
        this.mapMainView.setZoomWithTouchEventCenter(enabled);
    }

    public void setRotateWithTouchEventCenterEnabled(boolean enabled) {
        this.mapMainView.setRotateWithTouchEventCenter(enabled);
    }

    public void translateBy(float x, float y) {
        this.mapMainView.translateBy(x, y);
    }

    public void setCurrentZoomValue(float zoom, float pivotX, float pivotY) {
        this.mapMainView.setCurrentScale(zoom, pivotX, pivotY);
    }

    public void setCurrentZoomValue(float zoom) {
        setCurrentZoomValue(zoom, mapMainView.getWidth() / 2, mapMainView.getHeight() / 2);
    }

    public void setCurrentRotationDegrees(float degrees) {
        setCurrentRotationDegrees(degrees, mapMainView.getWidth() / 2, mapMainView.getHeight() / 2);
    }

    public void setCurrentRotationDegrees(float degrees, float pivotX, float pivotY) {
        this.mapMainView.setCurrentRotation(degrees, pivotX, pivotY);
    }

    public void setMaxZoomValue(float maxZoomValue) {
        this.mapMainView.setMaxScale(maxZoomValue);
    }

    public void sparkAtPoint(PointF point, float radius, int color, int repeatTimes) {
        this.mapMainView.sparkAtPoint(point, radius, color, repeatTimes);
    }

    public static interface MarkPointListener {

        public void mark(float x, float y);

    }
}
