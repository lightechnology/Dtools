package com.util.svgmap.core.componet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.util.svgmap.SVGMapView;
import com.util.svgmap.SVGMapViewListener;
import com.util.svgmap.core.helper.map.SVGBuilder;
import com.util.svgmap.overlay.SVGMapBaseOverlay;

import java.util.ArrayList;
import java.util.List;


public class MapMainView extends SurfaceView implements Callback {

    private static final String TAG = "MapMainView";

    private static final long LONG_TOUCH_TIME = 1000L;      // 长按最短时间
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private int mode = NONE;

    private SVGMapViewListener mapViewListener = null;
    private SurfaceHolder surfaceHolder;
    private List<SVGMapBaseOverlay> layers;
    private MapOverlay mapOverlay;
    private SparkOverlay sparkOverlay;
    private SVGMapView svgMapView;

    private Rect dirty = null;

    private boolean isMapLoadFinsh = false;

    private float minScale = 1.0f;
    private float maxScale = 5.0f;

    private Matrix matrix = new Matrix();                  // 当前地图应用的矩阵变化
    private Matrix savedMatrix = new Matrix();            // 保存手势Down下时的矩阵

    private PointF touch = new PointF();                   // 双击记录第一次点击位置
    private PointF start = new PointF();                   // 手势触摸的起始点
    private PointF mid = new PointF();                     // 双指手势的中心点

    private float currentRotation = 0f;                  // 当前旋转角度
    private float rotation = 0f;                          // 旋转角度
    private float currentDist = 0f;                       // 当前缩放距离

    private float currentScale = 1f;                      // 当前缩放比例
    private float matrixScale= 1f;                        // 缩放比例尺基数

    private long touchTime = 0L;                           // 按下时间
    private long releaseTouchTime = 0L;                   // 第一次松开时间

    private boolean dragGestureEnabled = true;                  // 可否拖动
    private boolean zoomGestureEnabled = true;                  // 可否缩放
    private boolean rotateGestureEnabled = true;                // 可否旋转
    private boolean zoomWithTouchEventCenter = false;          // 缩放绕两点中点
    private boolean rotateWithTouchEventCenter = false;        // 旋转绕两点中点

    public MapMainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMapView();
    }

    /**
     * 初始地图图层
     */
    private void initMapView() {
        layers = new ArrayList<SVGMapBaseOverlay>() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean add(SVGMapBaseOverlay overlay) {
                synchronized (this) {
                    if (this.size() != 0) {
                        if (overlay.getShowLevel() >= this.get(this.size() - 1).getShowLevel()) {
                            super.add(overlay);
                        } else {
                            for (int i = 0; i < this.size(); i++) {
                                if (overlay.getShowLevel() <= this.get(i).getShowLevel()) {
                                    super.add(i, overlay);
                                    break;
                                }
                            }
                        }
                    } else {
                        super.add(overlay);
                    }
                }
                return true;
            }

            @Override
            public void clear() {
                super.clear();
                MapMainView.this.mapOverlay = null;
            }
        };
        getHolder().addCallback(this);
    }

    /**
     * 初始画布
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(-1);
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * 画布旋转时，即屏幕转化时画布内容重画
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.surfaceHolder = holder;
        if (dirty == null || dirty.bottom == 0 || dirty.right == 0) {
            dirty = new Rect(0, 0, this.getWidth(), this.getHeight());
        }
        if (surfaceHolder != null) {
            this.refresh();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * 对地图操作，拖拉、缩放、旋转、双击放大，长按POI显示、短按POI显示
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isLongTouch = false;
        if (!isMapLoadFinsh || mapOverlay == null) {
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchTime = event.getEventTime();
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                currentDist = spaceBetweenTwoEvents(event);
                currentRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
                break;
            case MotionEvent.ACTION_UP:
                doubleTouch(event);
                layerTap(event);
                mode = NONE;
                this.refresh();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                savedMatrix.set(matrix);
                // 限制缩放状态记录
                matrixScale = currentScale;
                break;
            case MotionEvent.ACTION_MOVE:
                operateMove(event);
                this.refresh();
                break;
        }
        return true;
    }

    public void onDestroy() {
        try {
            for (int i = 0; i < layers.size(); i++) {
                layers.get(i).onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        try {
            for (int i = 0; i < layers.size(); i++) {
                layers.get(i).onPause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        try {
            for (int i = 0; i < layers.size(); i++) {
                layers.get(i).onResume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 地图加载
     *
     * @param svgString
     */
    public void loadMap(final String svgString) {
        isMapLoadFinsh = false;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Picture picture = new SVGBuilder().readFromString(svgString).build().getPicture();
                if (picture != null) {
                    if (MapMainView.this.mapOverlay == null) {
                        MapMainView.this.mapOverlay = new MapOverlay(MapMainView.this);
                        MapMainView.this.getOverLays().add(mapOverlay);
                    }
                    MapMainView.this.mapOverlay.setData(picture);
                    Log.i(TAG, "mapLoadFinished");
                    if (mapViewListener != null) {
                        mapViewListener.onMapLoadComplete();
                    }
                    isMapLoadFinsh = true;
                } else {
                    if (mapViewListener != null) {
                        mapViewListener.onMapLoadError();
                    }
                }
            }
        }.start();
    }

    /**
     * 画布刷新
     */
    public void refresh() {
        try {
            if (surfaceHolder != null) {
                synchronized (this.surfaceHolder) {
                    Canvas canvas = surfaceHolder.lockCanvas(dirty);
                    if (canvas != null) {
                        canvas.drawColor(-1);
                        for (int i = 0; i < layers.size(); i++) {
                            if (layers.get(i).isVisible()) {
                                layers.get(i).draw(canvas, matrix, currentScale, currentRotation);
                            }
                        }
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SVGMapBaseOverlay> getOverLays() {
        return this.layers;
    }

    public void setSvgMapView(SVGMapView svgMapView) {
        this.svgMapView = svgMapView;
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public float getCurrentRotation() {
        return currentRotation;
    }

    /**
     * 设置地图当前转角，按照中心点转
     *
     * @param degrees
     * @param pivotX
     * @param pivotY
     */
    public void setCurrentRotation(float degrees, float pivotX, float pivotY) {
        matrix.postRotate(-currentRotation + degrees, pivotX, pivotY);
        currentRotation = degrees;
        setCurrentRotateDegreesWithRule();
        refresh();
        mapCenter(true, true);
    }

    public float getCurrentScale() {
        return currentScale;
    }

    /**
     * 设置地图缩放比，按照给点坐标缩放
     *
     * @param scale
     * @param pivotX
     * @param pivotY
     */
    public void setCurrentScale(float scale, float pivotX, float pivotY) {
        matrix.postScale(scale / currentScale, scale / currentScale, pivotX, pivotY);
        matrixScale = this.currentScale = scale;
        refresh();
    }

    public void setRotateWithTouchEventCenter(boolean rotateWithTouchEventCenter) {
        this.rotateWithTouchEventCenter = rotateWithTouchEventCenter;
    }

    public void setZoomWithTouchEventCenter(boolean zoomWithTouchEventCenter) {
        this.zoomWithTouchEventCenter = zoomWithTouchEventCenter;
    }

    public void setRotateGestureEnabled(boolean rotateGestureEnabled) {
        this.rotateGestureEnabled = rotateGestureEnabled;
    }

    public void setZoomGestureEnabled(boolean zoomGestureEnabled) {
        this.zoomGestureEnabled = zoomGestureEnabled;
    }

    public void setDragGestureEnabled(boolean dragGestureEnabled) {
        this.dragGestureEnabled = dragGestureEnabled;
    }

    public void registeMapViewListener(SVGMapViewListener mapViewListener) {
        this.mapViewListener = mapViewListener;
    }

    /**
     * 过滤旋转角度，大于一圈的余掉
     */
    private void setCurrentRotateDegreesWithRule() {
        if (currentRotation > 360) {
            currentRotation = currentRotation % 360;
        } else if (currentRotation < 0) {
            currentRotation = 360 + (currentRotation % 360);
        }
    }

    /**
     * 地图拖拽、缩放、旋转操作
     *
     * @param event
     */
    private void operateMove(MotionEvent event) {
        matrix.set(savedMatrix);
        switch (mode) {
            case DRAG:
                if (dragGestureEnabled)
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                break;
            case ZOOM:
                if (zoomGestureEnabled) {
                    float newDist = spaceBetweenTwoEvents(event);
                    float scale = newDist / currentDist;
                    scale = checkScaleSize(scale);
                    float scaleMx = this.getWidth() / 2;
                    float scaleMy = this.getHeight() / 2;
                    if (zoomWithTouchEventCenter) {
                        scaleMx = mid.x;
                        scaleMy = mid.y;
                    }
                    matrix.postScale(scale, scale, scaleMx, scaleMy);
                }
                if (rotateGestureEnabled) {
                    rotation = rotation(event) - currentRotation;
                    float rotateMx = this.getWidth() / 2;
                    float rotateMy = this.getHeight() / 2;
                    if (rotateWithTouchEventCenter) {
                        rotateMx = mid.x;
                        rotateMy = mid.y;
                    }
                    matrix.postRotate(rotation, rotateMx, rotateMy);
                }
                break;
        }
    }

    /**
     * 地图放大
     *
     * @param event
     */
    private void scaleMap(MotionEvent event) {
        if (zoomGestureEnabled) {
            matrix.set(savedMatrix);
            float scale = checkScaleSize(1.5f);
            matrix.postScale(scale, scale, event.getX(), event.getY());
            // 限制缩放状态记录
            matrixScale = currentScale;
        }
    }

    /**
     * POI点击事件
     *
     * @param event
     */
    private void layerTap(MotionEvent event) {
        if (mode == DRAG && withFloorPlan(event.getX(), event.getY())) {
            try {
                for (int i = 0; i < layers.size(); i++) {
                    if (LONG_TOUCH_TIME < event.getEventTime() - touchTime) {
                        layers.get(i).onLongTap(event);
                    } else
                        layers.get(i).onTap(event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 双击事件记录
     *
     * @param event
     */
    private void doubleTouch(MotionEvent event) {
        if (0 == releaseTouchTime) {
            releaseTouchTime = event.getEventTime();
            touch.set(event.getX(), event.getY());
        } else {
            if (LONG_TOUCH_TIME / 2 > event.getEventTime() - releaseTouchTime
                    && 80 > Math.abs(event.getX() - touch.x)
                    && 80 > Math.abs(event.getY() - touch.y)) {
                scaleMap(event);
            }
            releaseTouchTime = 0L;
        }
    }

    /**
     * 两点旋转角度计算
     *
     * @param event
     * @return
     */
    private float rotation(MotionEvent event) {
        float delta_x = (event.getX(0) - event.getX(1));
        float delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 两点距离计算
     *
     * @param event
     * @return
     */
    private float spaceBetweenTwoEvents(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 两点中点计算
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 检查缩放比例，限制地图缩放
     *
     * @param scale
     * @return
     */
    private float checkScaleSize(float scale) {
        float result = (scale > 3.5f) ? 3.5f : scale;
        float ratio = this.matrixScale * scale;
        if (ratio < minScale)
        {
            ratio = minScale;
            result = ratio / this.matrixScale;
        }
        else if (ratio > maxScale)
        {
            ratio = maxScale;
            result = ratio / this.matrixScale;
        }
        this.currentScale = ratio;
        return result;
    }

    /**
     * 地图剧中显示
     *
     * @param horizontal
     * @param vertical
     */
    private void mapCenter(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF mapRect = new RectF(0, 0, this.mapOverlay.getFloorMap().getWidth(), this.mapOverlay.getFloorMap().getHeight());
        m.mapRect(mapRect);
        float width = mapRect.width();
        float height = mapRect.height();
        float deltaX = 0;
        float deltaY = 0;
        if (vertical) {
            if (height < this.getHeight()) {
                deltaY = (getHeight() - height) / 2 - mapRect.top;
            } else if (mapRect.top > 0) {
                deltaY = -mapRect.top;
            } else if (mapRect.bottom < getHeight()) {
                deltaY = getHeight() - mapRect.bottom;
            }
        }
        if (horizontal) {
            if (width < getWidth()) {
                deltaX = (getWidth() - width) / 2 - mapRect.left;
            } else if (mapRect.left > 0) {
                deltaX = -mapRect.left;
            } else if (mapRect.right < getWidth()) {
                deltaX = getWidth() - mapRect.right;
            }
            matrix.postTranslate(deltaX, deltaY);
        }
        refresh();
    }

    /**
     * 屏幕坐标转地图坐标
     *
     * @param x
     * @param y
     * @return
     */
    public float[] getMapCoordinateWithScreenCoordinate(float x, float y) {
        Matrix inverMatrix = new Matrix();
        float returnValue[] = {x, y};
        this.matrix.invert(inverMatrix);
        inverMatrix.mapPoints(returnValue);
        return returnValue;
    }

    /**
     * 截取当前地图
     */
    public void getCurrentMap() {
        try {
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bitCanvas = new Canvas(bitmap);
            for (SVGMapBaseOverlay layer : layers) {
                layer.draw(bitCanvas, matrix, currentScale, currentRotation);
            }
            if (mapViewListener != null) {
                mapViewListener.onGetCurrentMap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示地图视图在屏幕中心图钉
     */
    public void showCenterThumbtack() {
        Message message = new Message();
        message.what = 1;
        message.obj = new float[]{this.getWidth(), this.getHeight()};
        svgMapView.handler.sendMessage(message);
    }

    /**
     * 判断点是否在地图范围内
     *
     * @param x
     * @param y
     * @return
     */
    public boolean withFloorPlan(float x, float y) {
        float[] goal = getMapCoordinateWithScreenCoordinate(x, y);
        return goal[0] > 0 && goal[0] < mapOverlay.getFloorMap().getWidth() && goal[1] > 0
                && goal[1] < mapOverlay.getFloorMap().getHeight();
    }

    /**
     * 移动地图到x, y点
     *
     * @param x
     * @param y
     */
    public void translateBy(float x, float y) {
        this.matrix.postTranslate(x, y);
    }

    /**
     * 指定点位显示闪点
     *
     * @param point
     * @param radius
     * @param color
     * @param repeatTimes
     */
    public void sparkAtPoint(PointF point, float radius, int color, int repeatTimes) {
        sparkOverlay = new SparkOverlay(this, radius, point, color, repeatTimes);
        this.layers.add(sparkOverlay);
    }
}
