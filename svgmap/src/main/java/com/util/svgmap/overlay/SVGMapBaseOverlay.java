package com.util.svgmap.overlay;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;


public abstract class SVGMapBaseOverlay {

    protected static final int MAP_LEVEL = 0;
    protected static final int LOCATION_LEVEL = Integer.MAX_VALUE;

    protected int showLevel;
    protected boolean isVisible = true;
    protected float x, y;
    protected float width, height;

    public abstract void onDestroy();

    public abstract void onPause();

    public abstract void onResume();

    public abstract void onTap(MotionEvent event);

    public abstract void onLongTap(MotionEvent event);

    public abstract void draw(Canvas canvas, Matrix matrix, float currentZoom, float currentRotateDegrees);

    public abstract Integer[] getWH();

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getShowLevel() {
        return showLevel;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
