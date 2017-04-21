package com.util.svgmap;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.util.svgmap.core.componet.MapMainView;
import com.util.svgmap.overlay.SVGMapBaseOverlay;

import java.util.List;


public class SVGMapView extends FrameLayout implements View.OnTouchListener {
    private Context context;
    private MapMainView mapMainView;

    private SVGMapController mapController;

    private ImageView brandImageView;
    private ImageView centerImageView;

    float centerX, centerY;
    private SVGMapController.MarkPointListener markPointListener;

    public final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (1 == msg.what) {
                float[] xy = (float[]) msg.obj;
                showCenterThumbtack(xy[0], xy[1]);
            }
        }
    };

    public SVGMapView(Context context) {
        this(context, null);
    }

    public SVGMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVGMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        mapMainView = new MapMainView(context, attrs, defStyle);
        mapMainView.setSvgMapView(this);
        addView(mapMainView);

        brandImageView = new ImageView(context, attrs, defStyle);
        brandImageView.setScaleType(ScaleType.FIT_START);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics()));
        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        addView(brandImageView, params);

        centerImageView = new ImageView(context, attrs, defStyle);
        centerImageView.setScaleType(ScaleType.FIT_START);
        centerImageView.setOnTouchListener(this);
    }

    public SVGMapController getController() {
        if (this.mapController == null) {
            this.mapController = new SVGMapController(this);
        }
        return this.mapController;
    }

    public void setCenterPoint(Bitmap bitmap) {
        this.centerImageView.setImageBitmap(bitmap);
    }

    public void registerMarkPointListener(SVGMapController.MarkPointListener markPointListener) {
        this.markPointListener = markPointListener;
    }

    public void registerMapViewListener(SVGMapViewListener idrMapViewListener) {
        this.mapMainView.registeMapViewListener(idrMapViewListener);
    }

    public void loadMap(String svgString) {
        this.mapMainView.loadMap(svgString);
    }

    public void setBrandBitmap(Bitmap bitmap) {
        this.brandImageView.setImageBitmap(bitmap);
    }

    public void refresh() {
        this.mapMainView.refresh();
    }

    public void getCurrentMap() {
        this.mapMainView.getCurrentMap();
    }

    public float getCurrentRotateDegrees() {
        return this.mapMainView.getCurrentRotation();
    }

    public float getCurrentZoomValue() {
        return this.mapMainView.getCurrentScale();
    }

    public float getMaxZoomValue() {
        return this.mapMainView.getMaxScale();
    }

    public float getMinZoomValue() {
        return this.mapMainView.getMinScale();
    }

    public float[] getMapCoordinateWithScreenCoordinate(float screenX, float screenY) {
        return this.mapMainView.getMapCoordinateWithScreenCoordinate(screenX, screenY);
    }

    public List<SVGMapBaseOverlay> getOverLays() {
        return this.mapMainView.getOverLays();
    }

    public void onDestroy() {
        this.mapMainView.onDestroy();
    }

    public void onPause() {
        this.mapMainView.onPause();
    }

    public void onResume() {
        this.mapMainView.onResume();
    }

    public void showCenterThumbtack(float width, float height) {
        if (null != centerImageView.getDrawable()) {
            centerX = width / 2;
            centerY = height / 2;
            float cx = centerX - dip2px(15);
            float cy = centerY;
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics()));
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, cx, context.getResources().getDisplayMetrics());
            params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, cy, context.getResources().getDisplayMetrics());
            centerImageView.setLayoutParams(params);
            addView(centerImageView);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (null != markPointListener && null != centerImageView.getDrawable()) {
                    if (event.getX() <= dip2px(30) && event.getY() <= dip2px(30)) {
                        markPointListener.mark(centerX, centerY);
                    }
                }
                break;
        }
        return true;
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dip(float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
